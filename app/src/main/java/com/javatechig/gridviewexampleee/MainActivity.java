package com.javatechig.gridviewexampleee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import android.app.Dialog;
import android.app.WallpaperManager;
import android.os.Bundle;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends ActionBarActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    ArrayList<ImageItem> list;
    Bitmap bitmap;
    String name;
    //ImageItem currentImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("images");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    list = new ArrayList<>();
                    for (DataSnapshot im : dataSnapshot.getChildren()) {

                        ImageItem value = im.getValue(ImageItem.class);
                        list.add(value);
                    }


                    System.out.print("New List: ");
                    for(int i=0; i< list.size(); i++ ){
                        System.out.print(list.get(i).getName()+", ");
                    }
                    System.out.println("");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError);
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);
        try {

            gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, final long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                ImageItem image = getImagItemFromList(item.getId(), list);
                if(image == null)
                    image = new ImageItem();

                final Dialog d = new Dialog(MainActivity.this);
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.dialog_custom);
                final ImageView imageView = (ImageView) d.findViewById(R.id.image);
                TextView text = (TextView) d.findViewById(R.id.text);

                //Create intent
/*                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("title", item.getTitle());
                //Uri tempUri = getImageUri(getApplicationContext(), item.getImage());
                intent.putExtra("image", item.getImage());

                //Start details activity
                startActivity(intent);*/

                //ImageView image = (ImageView) d.findViewById(R.id.imageView);
                //image.setBackgroundResource(R.drawable.bgmain);
                //image.setImageBitmap(item.getImage());
                //image.setImageResource(R.drawable.a1hd);
                //text.setText("Captain America");

                //final String urlString = list.get(Integer.parseInt(item.getId())).getUrl();
                //getImageFromFirebase(item.getId());
                final String urlString = image.getUrl();
                bitmap = getBitmapFromURL(urlString);
                imageView.setImageBitmap(bitmap);
                name = image.getName();
                //name = currentImage.getName();
                text.setText(name);
                d.show();
/*                 Glide.
                        with(MainActivity.this).
                        load(url).
                        into(image);*/
                Button button = (Button) d.findViewById(R.id.dialogButtonOK);
                Button buttonSet = (Button) d.findViewById(R.id.dialogButtonSet);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        System.out.println("butona basıldıasfjajlfjasl");
                        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
                            @Override
                            public void onError(BasicImageDownloader.ImageError error) {
                                Toast.makeText(MainActivity.this, "Error code " + error.getErrorCode() + ": " +
                                        error.getMessage(), Toast.LENGTH_LONG).show();
                                error.printStackTrace();
                            }

                            @Override
                            public void onProgressChange(int percent) {

                            }

                            @Override
                            public void onComplete(Bitmap result) { System.out.println("buraya geldi");

                                /* save the image - I'm gonna use JPEG */
                                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                                        File.separator + "SuperHeroWallpaper" + File.separator + name +id + "." + mFormat.name().toLowerCase());
                                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                                    @Override
                                    public void onBitmapSaved() {
                                        Toast.makeText(MainActivity.this, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                                        Toast.makeText(MainActivity.this, "Error code " + error.getErrorCode() + ": " +
                                                error.getMessage(), Toast.LENGTH_LONG).show();
                                        error.printStackTrace();
                                    }


                                }, mFormat, false);


                                //imgDisplay.setImageBitmap(result);
                                //imgDisplay.startAnimation(AnimationUtils.loadAnimation(ImageActivity.this, android.R.anim.fade_in));

                            }
                        });
                        downloader.download(urlString, true);
                    }

                });

                buttonSet.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                        System.out.println("Set butona basıldıasfjajlfjasl");
                        WallpaperManager myWallpaperManager
                                = WallpaperManager.getInstance(getApplicationContext());
                        try {
                           // myWallpaperManager.setResource(imageView);
                            myWallpaperManager.setBitmap(bitmap);
                            Toast.makeText(MainActivity.this, "Setting wallpaper is succesfull.", Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                });
            }
        });


    }

    @Override
    public void onBackPressed() {

        android.os.Process.killProcess(android.os.Process.myPid());
        // This above line close correctly
    }



    public static Bitmap getBitmapFromURL(String src) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static ImageItem getImagItemFromList(String id, ArrayList<ImageItem> list){
        for (ImageItem item : list) {
            if(item.getId().equals(id)){
                return item;
            }
        }
        return null;
    }

/*  public void getImageFromFirebase(String id){
      System.out.println("id------> "+id);
      //currentImage = new ImageItem();
      final ImageItem[] value = new ImageItem[1];
      DatabaseReference db = FirebaseDatabase.getInstance().getReference();
      DatabaseReference images = db.child("images");
      Query query = images.orderByChild("id");
      //DatabaseReference idRef = ref.child("images");

      query.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists()) {
                  ArrayList<ImageItem> images = new ArrayList<ImageItem>();
                  for (DataSnapshot image : dataSnapshot.getChildren()) {
                      images.add(image.getValue(ImageItem.class));
                  }
                  System.out.println("size------> "+images.size());
              }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {
              System.out.println("error------> "+databaseError.getMessage());
          }
      });
  }*/

/*    public void writeNewUser(String id ) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        currentImage = new ImageItem();
        //ref.child("images").child(id).setValue(currentImage);
        ref.child("users").child(id).child("id").setValue(currentImage.getId());
        ref.child("users").child(id).child("name").setValue(currentImage.getName());
        ref.child("users").child(id).child("url").setValue(currentImage.getUrl());

    }*/

    /**
     * Prepare data for gridview
     */
    private ArrayList<ImageItem> getData() throws ExecutionException, InterruptedException {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < imgs.length(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
            String[]tokens = imgs.getString(i).split("/|\\.");
            imageItems.add(new ImageItem(bitmap ,tokens[2] , " "," "));
        }
        ArrayList<ImageItem> temp = imageItems;
        long seed = System.nanoTime();
        Collections.shuffle(temp, new Random(seed));
        imageItems.addAll(temp);
        long seed1 = System.nanoTime();
        Collections.shuffle(temp, new Random(seed1));
        imageItems.addAll(temp);
        return imageItems;
    }

}