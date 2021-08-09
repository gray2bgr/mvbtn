package com.example.movebtn;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    float dx,dy;
    DisplayMetrics dm = new DisplayMetrics();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button)findViewById(R.id.test_move);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    dx = motionEvent.getX();
                    dy = motionEvent.getY();

                } else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE){
                    int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
                    int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
                    int[] location = new int[2];
                    btn.getLocationOnScreen(location);
                    view.setX(motionEvent.getRawX() - dx);
                    TextView tv = (TextView)findViewById(R.id.show);

                    ImageView iv = (ImageView)findViewById(R.id.photo);


                    Bitmap xyjy = BitmapFactory.decodeResource(getResources(),R.drawable.xyjy);
                    Bitmap scaleBp = Bitmap.createScaledBitmap(xyjy,screenWidth,screenHeight, true);
                    Bitmap gray = convertGreyImg(scaleBp);
                    float ratio = (float)(location[0]+10) / (float)screenWidth;
                    iv.setImageBitmap(merge(gray,scaleBp,1 - ratio));
//                    view.setY(motionEvent.getRawY()- dy - getStatusBarHeight() - getTitleBarHeight());
                    tv.setText(String.valueOf(location[0])+" "+String.valueOf(screenWidth)+" "+String.valueOf(ratio));
                }
                return true;
            }
        });
    }
    public int getStatusBarHeight(){
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getTitleBarHeight(){
        Window window = getWindow();
        int contentViewTop = getWindow()
                .findViewById(Window.ID_ANDROID_CONTENT).getTop();
        // statusBarHeight是上面所求的状态栏的高度
        int titleBarHeight = contentViewTop - getStatusBarHeight();
        return titleBarHeight;
    }

    /**
     * 将彩色图转换为灰度图
     * @param img 位图
     * @return  返回转换好的位图
     */
    public Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for(int i = 0; i < height; i++)  {
            for(int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey  & 0x00FF0000 ) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int)((float) red * 0.3 + (float)green * 0.59 + (float)blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    public Bitmap merge(Bitmap bitmap_left,Bitmap bitmap_right, float ratio)
    {
        int width = bitmap_left.getWidth();
        int height = bitmap_left.getHeight();
        Log.e(TAG, "merge: "+String.valueOf(width)+" "+String.valueOf(height));
        int split = (int) (ratio * width);
        int needX = width - split;
        Bitmap result = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Bitmap cropBitmap = Bitmap.createBitmap(bitmap_right, needX, 0, split, bitmap_right.getHeight());

        canvas.drawBitmap(bitmap_left,0,0,null);
        canvas.drawBitmap(cropBitmap,width-split,0,null);
        Bitmap line = Bitmap.createBitmap(3,height,Bitmap.Config.ARGB_8888);
        line.eraseColor(Color.parseColor("#00ff3c"));
        canvas.drawBitmap(line,width-split,0,null);
        return result;
    }

}