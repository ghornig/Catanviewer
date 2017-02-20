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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {

    private RelativeLayout mMainLayout;
    private InteractiveView mInteractiveView;

    private int mScreenWidth;
    private int mScreenHeight;
    private int tileW = 150;
    private int tileH = (int)(tileW * Math.sqrt(3) / 2);
    private int tokenW = (int)(0.4*tileW);


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

        mInteractiveView.removeAllViews();
        int hexSide = this.tileW/2;
        // For testing:
        int originX = 300;
        int originY = 700;
        int tileCenterX = (int)((this.tileW/2) - this.tokenW/2);
        int tileCenterY = (int)((this.tileH/2) - this.tokenW/2);

        int[] tileID = new int[19];
        int[] tokenID = new int[19];
        int[] tokenStock = new int[13]; //Using literal token number for each stock (there will be blanks)
        // Count how many of each: (this should depend on which version of the game you are playing)
        tokenStock[0] = 0;
        tokenStock[1] = 0;
        tokenStock[2] = 1;
        tokenStock[3] = 2;
        tokenStock[4] = 2;
        tokenStock[5] = 2;
        tokenStock[6] = 2;
        tokenStock[8] = 2;
        tokenStock[9] = 2;
        tokenStock[10] = 2;
        tokenStock[11] = 2;
        tokenStock[12] = 1;
        /* for (int i = 3; i < 12; i++) {
            tokenStock[i] = 2;
        }
        tokenStock[7] = 0; */

        int[] tileStock = new int[6];
        tileStock[0] = 1; // Desert
        tileStock[1] = 4; // Forest
        tileStock[2] = 3; // Ore
        tileStock[3] = 4; // Pasture
        tileStock[4] = 4; // Wheat
        tileStock[5] = 3; // Brick


        BoardTile[] tileHandle = new BoardTile[19];
        BoardTile[] tokenHandle = new BoardTile[19];

        // List to store the tiles that you can use
        List<Integer> tileSelection = new ArrayList<Integer>();
        // Generate the tiles:
        Random rand = new Random();
        for (int j =0; j < 19; j++) {

            // Check which tiles can be added
            for (int i = 0; i < 6; i++) {
                if (tileStock[i] != 0) {
                    tileSelection.add(i);
                }
            }

            // Choose a tile from the list of tiles that can be added
            tileID[j] = tileSelection.get(new Random().nextInt(tileSelection.size()));
            // Remove one from the stock that you used and clear the list for the next tile choice.
            tileStock[tileID[j]] = tileStock[tileID[j]] - 1;
            tileSelection.clear();
        }
        // (For testing):
        // int[] tileID = {1, 3, 0, 1, 3, 4, 0, 2, 1, 3, 0, 2, 3, 4, 1, 0, 0, 1, 2};

        List<Integer> tokenSelection = new ArrayList<Integer>();
        // Generate the tokens:
        for (int j =0; j < 19; j++) {
            // Check if it is the desert (no tile assigned.)
            if (tileID[j] == 0) {
                tokenID[j] = -1;
            } else {
                // Check which tokens can be added
                for (int k = 0; k < 13; k++) {
                    if (tokenStock[k] != 0) {
                        tokenSelection.add(k);
                    }
                }


                tokenID[j] = tokenSelection.get(new Random().nextInt(tokenSelection.size()));

                tokenStock[tokenID[j]] = tokenStock[tokenID[j]] - 1;
                if (tokenSelection.size() != 0) {
                    tokenSelection.clear();
                }

            }

        }

        int[] position;
        // Put together the board resource tiles depending on the configuration
        for (int i = 0; i < 19; i++) {
            position = getTileCoord("basic", i, hexSide);
            // Adding a tile we can move on the top of the board (defined below)
            Bitmap thisTileImage = findTile(tileID[i]);
            tileHandle[i] = addElement(position[0]+originX, position[1]+originY, thisTileImage);

            // Add the token, but compensate to put it in the center of the tile.
            // Not the desert...
            if (tokenID[i] != -1) {
                Bitmap thisTokenImage = findToken(tokenID[i]);
                tokenHandle[i] = addElement(position[0] + originX + tileCenterX, position[1] + originY + tileCenterY, thisTokenImage);
            }
        }

    }


    // Creation of a smaller element
    private BoardTile addElement(int pPosX, int pPosY, Bitmap lImage) {

        // Make the tile and set the image
        BoardTile lBoardTile = new BoardTile(this);
        lBoardTile.setImage(lImage);

        // Create object specifically for storing position...
        Point lPoint = new Point();
        lPoint.x = pPosX;
        lPoint.y = pPosY;
        // Move the tile to this position
        lBoardTile.setPosition(lPoint);

        // Add this tile view to the board
        mInteractiveView.addView(lBoardTile);

        // Return the handle so you can use it later. (this does what I think it does, right...)
        return lBoardTile;
    }

    // Gives the coordinates of each tile depending on what board you are using.
    // Uses tile 0 as origin
    private int[] getTileCoord(String boardConfig, int tileNo, int hexSide) {
        int xCoord = 0;
        int yCoord = 0;
        // What percent of the width is the border so that it can be compensated and overlapped.
        float borderXpercent = 26/2000;
        float borderYpercent = 33/1730;
        switch (boardConfig) {
            case "basic":
                if (tileNo <= 2) {
                    xCoord = (int) (tileNo * (1.5) * hexSide) - (int)(tileNo*borderXpercent*this.tileW);
                    yCoord = (int) (tileNo * -(Math.sqrt(3) /2 * hexSide)-(int)(tileNo*borderYpercent*(this.tileW * Math.sqrt(3)/2)));
                } else if ((tileNo <= 6) && (tileNo >= 3)) {
                    xCoord = (int) ((tileNo - 3) * (1.5) * hexSide)- (int)(tileNo*borderXpercent*this.tileW);
                    yCoord = (int) (((tileNo - 3) * -(Math.sqrt(3)/2 * hexSide) + Math.sqrt(3) * hexSide)-(int)(tileNo*borderYpercent*(this.tileW * Math.sqrt(3)/2)));
                } else if ((tileNo <= 11) && (tileNo >= 7)) {
                    xCoord = (int) ((tileNo - 7) * (1.5) * hexSide)- (int)(tileNo*borderXpercent*this.tileW);
                    yCoord = (int) ((tileNo - 7) * -(Math.sqrt(3)/2 * hexSide) + 2 * Math.sqrt(3) * hexSide)-(int)(tileNo*borderYpercent*(this.tileW * Math.sqrt(3)/2));
                } else if ((tileNo <= 15) && (tileNo >= 12)) {
                    xCoord = (int) ((tileNo - 12 + 1) * (1.5) * hexSide)- (int)(tileNo*borderXpercent*this.tileW);
                    yCoord = (int) ((tileNo - 12 - 1) * -(Math.sqrt(3) / 2 * hexSide) + 2 * Math.sqrt(3) * hexSide)-(int)(tileNo*borderYpercent*(this.tileW * Math.sqrt(3)/2));
                } else if ((tileNo <= 18) && (tileNo >= 16)) {
                    xCoord = (int) ((tileNo - 16 + 2) * (1.5) * hexSide) - (int) (tileNo * borderXpercent * this.tileW);
                    yCoord = (int) ((tileNo - 16 - 2) * -(Math.sqrt(3) / 2 * hexSide) + 2 * Math.sqrt(3) * hexSide) - (int) (tileNo * borderYpercent * (this.tileW * Math.sqrt(3) / 2));

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

    // Returns the tile bitmap based on its index
    private Bitmap findTile(int tileType) {
        int resourceID = -1;

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
                resourceID = R.drawable.tile_brick;
                break;
            case 6:
                resourceID = R.drawable.tile_water;
                break;
            case 7:
                resourceID = R.drawable.tile_gold;
                break;
        }

        Bitmap lSourceImage = BitmapFactory.decodeResource(getResources(), resourceID);
        Bitmap lImage = Bitmap.createScaledBitmap(lSourceImage, this.tileW, this.tileH, true);

        return lImage;
    }

    // Returns the number token bitmap based on its index
    private Bitmap findToken(int tokenNum) {
        int resourceID = -1;

        switch (tokenNum) {
            case 2:
                resourceID = R.drawable.token_2;
                break;
            case 3:
                resourceID = R.drawable.token_3;
                break;
            case 4:
                resourceID = R.drawable.token_4;
                break;
            case 5:
                resourceID = R.drawable.token_5;
                break;
            case 6:
                resourceID = R.drawable.token_6;
                break;
            case 8:
                resourceID = R.drawable.token_8;
                break;
            case 9:
                resourceID = R.drawable.token_9;
                break;
            case 10:
                resourceID = R.drawable.token_10;
                break;
            case 11:
                resourceID = R.drawable.token_11;
                break;
            case 12:
                resourceID = R.drawable.token_12;
                break;
        }

        Bitmap lSourceImage = BitmapFactory.decodeResource(getResources(), resourceID);
        return Bitmap.createScaledBitmap(lSourceImage, this.tokenW, this.tokenW, true);

    }

}