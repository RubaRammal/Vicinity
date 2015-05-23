package vicinity.vicinity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import vicinity.ConnectionManager.UdpBroadcastManager;
import vicinity.Controller.MainController;
import vicinity.model.Globals;
import vicinity.model.Post;

/**
 * An Activity to add a new post.
 */
public class NewPostActivity extends ActionBarActivity {

    private static final String TAG = "NewPost";
    private EditText postTextField ;
    private MainController mc ;
    private UdpBroadcastManager broadcastManager;
    private static final int SELECT_PICTURE_ACTIVITY_REQUEST_CODE = 0;
    private Post aPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


    /*----------Change the style of the ActionBar------------*/
        final ActionBar abar = getSupportActionBar();
        abar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#01aef0")));//line under the action bar
        View viewActionBar = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = (TextView) viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("New Post");//ActionBar title
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        abar.setDisplayHomeAsUpEnabled(false);
        abar.setHomeButtonEnabled(true);


        // Initialization
        mc = new MainController(this);
        broadcastManager = new UdpBroadcastManager();
        postTextField = (EditText) findViewById(R.id.postTextField);
        Button sendImgButton = (Button) findViewById(R.id.sendImageButton);
        sendImgButton.setEnabled(true);
        aPost = new Post();

        postTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //sendImgButton.setEnabled(!TextUtils.isEmpty(postTextField.getText().toString().trim()));
            }
        }); //END addTextChangedListener


        sendImgButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectPicture();

            }
        });//END onClickListener

    } //END onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_send_post:
                sendPost();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sendPost(){
        Random r = new Random();
        String postText = postTextField.getText().toString();
        try {
            //Can send post only if connected to a network
            if(Globals.isConnectedToANetwork){
                aPost.setPostBody(postText);
                aPost.setPostedBy(mc.retrieveCurrentUsername());
                aPost.setPostID(r.nextInt((1000 - 1) + 1) + 1);
                broadcastManager.setPost(aPost);
                broadcastManager.execute();
                finish();

            }
            else{
                CharSequence text = "You are not connected to a network!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(NewPostActivity.this, text, duration);
                toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                toast.show();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the filepath and bitmap of the selected image from gallery
     * @param requestCode helps identify the request
     * @param resultCode to check whether the result status is OK or CANCELLED
     * @param imageReturnedIntent carries the additional data (image)
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PICTURE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    if (cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String filePath = cursor.getString(columnIndex);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        bmOptions.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(filePath, bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;

                        Bitmap rotatedBitmap = decodeFile(new File(filePath),
                                photoW, photoH, getImageOrientation(filePath));
                      Log.i(TAG, "SHOULD BE ROTATED");
                        try {
                            sendPhotoObj(rotatedBitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    cursor.close();
                }
                break;
        }
    }

    /**
     * Changes the rotation of the image in order for
     * it to be displayed in an imageView correctly
     * @param imagePath  String of the image path
     * @return int value of the rotation
     */
    public static int getImageOrientation(String imagePath) {
        int rotate = 0;
        try {

            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static Bitmap decodeFile(File f, double REQUIRED_WIDTH,
                                    double REQUIRED_HEIGHT, int rotation) {
        try {
            if (REQUIRED_WIDTH == 0 || REQUIRED_HEIGHT == 0) {
                return BitmapFactory.decodeFile(f.getAbsolutePath());
            } else {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(f.getAbsolutePath(), o);

                o.inSampleSize = calculateInSampleSize(o, REQUIRED_WIDTH,
                        REQUIRED_HEIGHT);

                o.inJustDecodeBounds = false;
                o.inPurgeable = true;
                Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath(), o);
                if (rotation != 0)
                    b = rotate(b, rotation);
                if (b.getWidth() > REQUIRED_WIDTH
                        || b.getHeight() > REQUIRED_HEIGHT) {
                    double ratio = Math.max((double) b.getWidth(),
                            (double) b.getHeight())
                            / (double) Math
                            .min(REQUIRED_WIDTH, REQUIRED_HEIGHT);

                    return Bitmap.createScaledBitmap(b,
                            (int) (b.getWidth() / ratio),
                            (int) (b.getHeight() / ratio), true);
                } else
                    return b;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            double reqWidth, double reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((height / inSampleSize) > reqHeight
                    || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        inSampleSize = Math.max(1, inSampleSize / 2);
        return inSampleSize;
    }

    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2,
                    (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }

    /**
     * Creates the image attribute in aPost object that will be sent
     * @param b image to be sent bitmap
     */

    public void sendPhotoObj(Bitmap b)  {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap resized = Bitmap.createScaledBitmap(b,(int)(b.getWidth()*0.3), (int)(b.getHeight()*0.3), true);
        resized.compress(Bitmap.CompressFormat.JPEG, 5, baos);

        Log.i(TAG, resized.getHeight()* resized.getWidth()+"");
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        aPost.setBitmap(encodedImage);
        Toast.makeText(getApplicationContext(), "Photo attached to Post",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Opens the device media gallery and activates the ActivityForResult method
     */
    private void selectPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE_ACTIVITY_REQUEST_CODE);
    }


}