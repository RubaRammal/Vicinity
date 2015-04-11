package vicinity.model;

import android.content.Context;

import java.io.File;

/**
 * Created by AMAL on 4/11/15.
 */
public class Photo extends VicinityMessage{



        private File photoFile;
        private String photoPath;

        /**
         * Public constructor, initiates a message
         * attaches to it its timestamp and date
         *
         * @param context     activity context
         * @param friendID    string
         * @param isMyMsg     boolean
         * @param messageBody string
         */
        public Photo(Context context, String friendID, boolean isMyMsg, String messageBody, File photoFile) {
            super(context, friendID, isMyMsg, messageBody);
            this.photoFile = photoFile;

        }

        public boolean setphotoFile(File photofile) {
            this.photoFile = photofile;
            return true;
        }
        public File getphotoFile() {
            return this.photoFile;
        }


        public String getPhotoPath() {
            return photoPath;
        }

        public void setPhotoPath(String photoPath) {
            this.photoPath = photoPath;
        }


    }

