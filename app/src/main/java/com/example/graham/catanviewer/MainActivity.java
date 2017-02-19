// http://stackoverflow.com/questions/14750476/how-to-create-zoomable-pannable-view-with-many-draggable-views-on-top-of-it
package com.example.graham.catanviewer;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.graham.catanviewer.R;

import java.util.Random;

public class MainActivity extends Activity {

    private RelativeLayout mMainLayout;
    private InteractiveView mInteractiveView;

    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen mode
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Retrieve the device dimensions to adapt interface
        mScreenWidth = getApplicationContext().getResources()
                .getDisplayMetrics().widthPixels;
        mScreenHeight = getApplicationContext().getResources()
                .getDisplayMetrics().heightPixels;

        mMainLayout = (RelativeLayout) findViewById(R.id.activity_main);

        // Create the interactive view holding the elements
        mInteractiveView = new InteractiveView(this);
        mInteractiveView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
        mInteractiveView.setPosition(-mScreenWidth / 2, -mScreenHeight / 2);

        mMainLayout.addView(mInteractiveView);

        // Adding a background to this view
        ImageView lImageView = new ImageView(this);
        lImageView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        lImageView.setImageResource(R.drawable.board);

        mInteractiveView.addView(lImageView);


        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          generateLayout();
                                      }
                                  });

        generateLayout();


    }

    private void generateLayout() {

        int tileW = 150;
        int hexSide = tileW/2;
        // For testing:
        int originX = 300;
        int originY = 700;
        int[] tileID = new int[19];
        Random rand = new Random();
        for (int j =0; j < 19; j++) {
            tileID[j] = rand.nextInt(6); // SHould be 7, removed gold for demo cause it was too frequent and weird
        }
        // int[] tileID = {1, 3, 0, 1, 3, 4, 0, 2, 1, 3, 0, 2, 3, 4, 1, 0, 0, 1, 2};
        int[] position;
        // Put together the board depending on the configuration
        Log.d("notag", "Beginning to place element...");
        for (int i = 0; i < 19; i++) {
            position = getTileCoord("basic", i, hexSide);
            // Adding a tile we can move on the top of the board (defined below)
            addElement(position[0]+originX, position[1]+originY, tileID[i]);
        }

    }


    // Creation of a smaller element
    private void addElement(int pPosX, int pPosY, int tileID) {

        // Make the tile and set the image
        BoardTile lBoardTile = new BoardTile(this);
        Bitmap lImage = findTile(tileID);
        lBoardTile.setImage(lImage);

        // Create object specifically for storing position...
        Point lPoint = new Point();
        lPoint.x = pPosX;
        lPoint.y = pPosY;
        // Move the tile to this position
        lBoardTile.setPosition(lPoint);

        // Add this tile view to the board
        mInteractiveView.addView(lBoardTile);
    }

    // Gives the coordinates of each tile depending on what board you are using.
    // Uses tile 0 as origin
    private int[] getTileCoord(String boardConfig, int tileNo, int hexSide) {
        int xCoord = 0;
        int yCoord = 0;
        float borderXpercent = 26/2000;
        float borderYpercent = 33/1730;
        int tileW = 150;
        switch (boardConfig) {
            case "basic":
                if (tileNo <= 2) {
                    xCoord = (int) (tileNo * (1.5) * hexSide) - (int)(tileNo*borderXpercent*tileW);
                    yCoord = (int) (tileNo * -(Math.sqrt(3) /2 * hexSide)-(int)(tileNo*borderYpercent*(tileW * Math.sqrt(3)/2)));
                } else if ((tileNo <= 6) && (tileNo >= 3)) {
                    xCoord = (int) ((tileNo - 3) * (1.5) * hexSide)- (int)(tileNo*borderXpercent*tileW);
                    yCoord = (int) (((tileNo - 3) * -(Math.sqrt(3)/2 * hexSide) + Math.sqrt(3) * hexSide)-(int)(tileNo*borderYpercent*(tileW * Math.sqrt(3)/2)));
                } else if ((tileNo <= 11) && (tileNo >= 7)) {
                    xCoord = (int) ((tileNo - 7) * (1.5) * hexSide)- (int)(tileNo*borderXpercent*tileW);
                    yCoord = (int) ((tileNo - 7) * -(Math.sqrt(3)/2 * hexSide) + 2 * Math.sqrt(3) * hexSide)-(int)(tileNo*borderYpercent*(tileW * Math.sqrt(3)/2));
                } else if ((tileNo <= 15) && (tileNo >= 12)) {
                    xCoord = (int) ((tileNo - 12 + 1) * (1.5) * hexSide)- (int)(tileNo*borderXpercent*tileW);
                    yCoord = (int) ((tileNo - 12 - 1) * -(Math.sqrt(3) / 2 * hexSide) + 2 * Math.sqrt(3) * hexSide)-(int)(tileNo*borderYpercent*(tileW * Math.sqrt(3)/2));
                } else if ((tileNo <= 18) && (tileNo >= 16)) {
                    xCoord = (int) ((tileNo - 16 + 2) * (1.5) * hexSide) - (int) (tileNo * borderXpercent * tileW);
                    yCoord = (int) ((tileNo - 16 - 2) * -(Math.sqrt(3) / 2 * hexSide) + 2 * Math.sqrt(3) * hexSide) - (int) (tileNo * borderYpercent * (tileW * Math.sqrt(3) / 2));

                } else {
                    // Error
                }
                break;
        }
        int[] output = {0,0};
        output[0] = xCoord;
        output[1] = yCoord;
        return output;

    }

    // Returns the number of the last tile for a particular board (zero being the first tile)
    private int lastTile(String boardConfig) {
        int temp = 0;
        switch (boardConfig) {
            case "basic":
                temp = 18;
                break;
        }
        return temp;
    }

    private Bitmap findTile(int tileType) {
        int resourceID = -1;

        Log.d("notag", "Looking up tiles...");

        switch (tileType) {
            case 0:
                resourceID = R.drawable.tile_desert;
                break;
            case 1:
                resourceID = R.drawable.tile_forest;
                break;
            case 2:
                resourceID = R.drawable.tile_ore;
                break;
            case 3:
                resourceID = R.drawable.tile_pasture;
                break;
            case 4:
                resourceID = R.drawable.tile_wheat;
                break;
            case 5:
                resourceID = R.drawable.tile_water;
                break;
            case 6:
                resourceID = R.drawable.tile_gold;
                break;
        }

        Bitmap lSourceImage = BitmapFactory.decodeResource(getResources(), resourceID);
        int tileW = 150;
        Bitmap lImage = Bitmap.createScaledBitmap(lSourceImage, tileW, (int) (tileW * Math.sqrt(3) / 2), true);

        return lImage;
    }


}