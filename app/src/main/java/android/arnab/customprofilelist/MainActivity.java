package android.arnab.customprofilelist;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener,View.OnClickListener
{
    int images[]={R.drawable.prof1,R.drawable.prof2,R.drawable.prof3,R.drawable.prof4,R.drawable.prof5,R.drawable.prof6,R.drawable.prof7,R.drawable.prof8};
    de.hdodenhof.circleimageview.CircleImageView profileFrames[];
    static RelativeLayout profiles[],layout,cover;
    static int NO_OF_PROFILES=0,CHK_IF_RESUME=0,startX=0,startY=0,currentX=0,currentY=0,PROFILES_IN_SCREEN=5, HIGHEST=0;
    static float d,SCREEN_HEIGHT,SCREEN_WIDTH,SPEED=1.5f;
    float leftmargin[],topMargin[];
    static double distance=0;
    private boolean started = false,reCenterStarted=false,bgStarted=false,profileStarted=false;
    static long startTime=0,endTime=0,scrollStartTime=0,INTERVAL=5;
    Button reCenter;
    ImageView bg1,bg2;
    private Handler handler = new Handler();
    private Handler handlerRC=new Handler();
    private Handler handlerBG=new Handler();
    private Handler handlerProfile=new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        me.itangqi.waveloadingview.WaveLoadingView frontWaves=(me.itangqi.waveloadingview.WaveLoadingView)findViewById(R.id.frontWaves);
        frontWaves.setAmplitudeRatio(100);
        frontWaves.setAnimDuration(3000);

        final me.itangqi.waveloadingview.WaveLoadingView bgWaves=(me.itangqi.waveloadingview.WaveLoadingView)findViewById(R.id.bgWaves);
        bgWaves.setAnimDuration(5000);

        d = this.getResources().getDisplayMetrics().density;

        SCREEN_WIDTH=getWindowManager().getDefaultDisplay().getWidth();
        SCREEN_HEIGHT=getWindowManager().getDefaultDisplay().getHeight();
        //NO_OF_PROFILES=5+(int)(Math.random()*((300-5)+1));
        NO_OF_PROFILES=50;

        //Toast.makeText(this,NO_OF_PROFILES+"",Toast.LENGTH_LONG).show();

        layout=(RelativeLayout)findViewById(R.id.waveyRel);
        cover=(RelativeLayout)findViewById(R.id.cover);
        reCenter=(Button)findViewById(R.id.reCenter);
        bg1=(ImageView)findViewById(R.id.bg1);
        bg2=(ImageView)findViewById(R.id.bg2);
        reCenter.setOnClickListener(this);
        cover.setVisibility(View.GONE);

        bg2.setY(600*d);
        bg1.setY(bg2.getY()-bg1.getHeight());

        leftmargin=new float[NO_OF_PROFILES];
        topMargin=new float[NO_OF_PROFILES];
        profileFrames=new de.hdodenhof.circleimageview.CircleImageView[NO_OF_PROFILES];
        profiles=new RelativeLayout[NO_OF_PROFILES];
        layout.setOnTouchListener(this);


        startProfile();
        start();
        startBg();
    }
    public void startProfile()
    {
        //Toast.makeText(MainActivity.this,"in start",Toast.LENGTH_LONG).show();
        profileStarted = true;
        handlerProfile.postDelayed(runnableProfile, 5000);
    }
    private Runnable runnableProfile= new Runnable() {
        @Override
        public void run()
        {
            float tempX =100+(float)(Math.random()*(((SCREEN_WIDTH-200)-100)+1));
            float tempY=0;
            int chk=0,a;

            for (int b = 0; b < NO_OF_PROFILES; b++)
            {
                if(profiles[b]!=null)
                {
                    if (Math.abs(profiles[b].getX() - tempX) <= 250 && Math.abs(profiles[b].getY() - tempY) <= 400)
                    {
                        chk = 2;
                        break;
                    }
                }

            }


            for(a=0;a<NO_OF_PROFILES && chk!=2;a++)       //Check if free space available
            {
                if(profiles[a]==null)
                {
                    chk=1;
                    break;
                }
            }
            if(chk==1)          //Create profile
            {
                profiles[a]=new RelativeLayout(MainActivity.this);
                profiles[a].setX(tempX);
                profiles[a].setY(tempY);

                profileFrames[a]=new de.hdodenhof.circleimageview.CircleImageView(MainActivity.this);
                adjustProfileSize(a);
                profiles[a].setBackground(MainActivity.this.getResources().getDrawable(R.drawable.inverted_drop));
                layout.addView(profiles[a]);
                int imgNo=0+(int)(Math.random()*(((images.length-1)-0)+1));
                profileFrames[a].setImageResource(images[imgNo]);
                profileFrames[a].setBorderWidth(2);
                profileFrames[a].setBorderColor(Color.WHITE);

                final int val=a;
                profiles[a].addView(profileFrames[a]);
                profiles[a].setOnTouchListener(MainActivity.this);
                profileFrames[a].setOnTouchListener(MainActivity.this);
                profiles[a].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,ProfileDetails.class);
                        startActivity(intent);
                        profileFrames[val].setVisibility(View.GONE);
                        profiles[val].setVisibility(View.GONE);
                    }
                });
                profileFrames[a].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(MainActivity.this,ProfileDetails.class);
                        startActivity(intent);
                        profileFrames[val].setVisibility(View.GONE);
                        profiles[val].setVisibility(View.GONE);
                    }
                });
            }


            if(profileStarted)
            {
                //Toast.makeText(MainActivity.this,"in run",Toast.LENGTH_LONG).show();
                startProfile();
            }
        }
    };


    public void start()
    {
        //Toast.makeText(MainActivity.this,"in start",Toast.LENGTH_LONG).show();
        started = true;
        handler.postDelayed(runnable, INTERVAL);
    }
    private Runnable runnable= new Runnable() {
        @Override
        public void run()
        {

            for(int a=0;a<NO_OF_PROFILES;a++)
            {
                if(profiles[a]!=null)
                {
                    profiles[a].setY(profiles[a].getY() + SPEED);
                    adjustProfileSize(a);
                    if(profiles[a].getY()>SCREEN_HEIGHT+4000)
                    {
                        profileFrames[a]=null;
                        profiles[a]=null;
                    }
                }
            }

            if(started)
            {
                //Toast.makeText(MainActivity.this,"in run",Toast.LENGTH_LONG).show();
                start();
            }
        }
    };

    public void startReCenter()
    {
        //Toast.makeText(MainActivity.this,"in start",Toast.LENGTH_LONG).show();
        cover.setVisibility(View.VISIBLE);
        reCenterStarted = true;
        handlerRC.postDelayed(runnableRC, 5);
    }
    private Runnable runnableRC= new Runnable() {
        @Override
        public void run()
        {

            for(int a=0;a<NO_OF_PROFILES;a++)
            {
                if(profiles[a]!=null)
                {
                    profiles[a].setY(profiles[a].getY() + SPEED * 10);
                    adjustProfileSize(a);
                }
            }
            distance-=SPEED*10;

            if(reCenterStarted)
            {
                //Toast.makeText(MainActivity.this,"in run",Toast.LENGTH_LONG).show();
                if(distance>0)
                    startReCenter();
                else
                {
                    reCenterStarted = false;
                    cover.setVisibility(View.GONE);
                    start();
                }
            }
        }
    };

    public void startBg()
    {
        //Toast.makeText(MainActivity.this,"in start",Toast.LENGTH_LONG).show();
        bgStarted = true;
        handlerBG.postDelayed(runnableBg, INTERVAL+5);
    }
    private Runnable runnableBg= new Runnable() {
        @Override
        public void run()
        {

            bg1.setY(bg1.getY()+SPEED-0.2f);
            bg2.setY(bg2.getY()+SPEED-0.2f);
            if(bg1.getY()>SCREEN_HEIGHT-100*d)
            {
                //Toast.makeText(MainActivity.this,"in start up",Toast.LENGTH_LONG).show();
                bg1.setY(bg2.getY()-bg1.getHeight());
            }
            if(bg2.getY()>SCREEN_HEIGHT-100*d)
            {
                //Toast.makeText(MainActivity.this,"in start down",Toast.LENGTH_LONG).show();
                bg2.setY(bg1.getY()-bg2.getHeight());
            }

            if(bgStarted)
            {
                startBg();
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent)
    {
        final int action= motionEvent.getAction();
        switch(action & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                startTime= Calendar.getInstance().getTimeInMillis();
                setStartX((int)motionEvent.getRawX());
                setStartY((int)motionEvent.getRawY());
                if(scrollStartTime==0 || startTime-scrollStartTime>10000)
                {
                    scrollStartTime=startTime;
                    distance=0;
                }

                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                setCurrentX((int)motionEvent.getRawX());
                setCurrentY((int)motionEvent.getRawY());

                //Toast.makeText(this,"move",Toast.LENGTH_SHORT).show();

                float diffX=startX-currentX;
                float diffY=startY-currentY;

                setStartX(currentX);
                setStartY(currentY);

                if(diffY>0)
                {
                    distance+=diffY;
                    for (int a = 0; a < NO_OF_PROFILES; a++)
                    {
                        if(profiles[a]!=null)
                        {
                            profiles[a].setY(profiles[a].getY() - diffY);
                            adjustProfileSize(a);
                        }
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                endTime=Calendar.getInstance().getTimeInMillis();
                if(endTime-startTime<=150 && !v.equals(layout))
                    v.performClick();

                break;
            }

        }
        return true;
    }

    static void setCurrentX(int x)
    {
        currentX=x;
    }
    static void setCurrentY(int y)
    {
        currentY=y;
    }
    static void setStartX(int x)
    {
        startX=x;
    }
    static void setStartY(int y)
    {
        startY=y;
    }

    public void adjustProfileSize(int index)
    {
        int a=index;
        if(profiles[a]!=null && profileFrames[a]!=null) {
            if (profiles[a].getY() < 0)            //Above screen
            {
                profiles[a].setLayoutParams(new RelativeLayout.LayoutParams(0, 0));

                profileFrames[a].setLayoutParams(new RelativeLayout.LayoutParams(0, 0));


            } else if (profiles[a].getY() >= SCREEN_HEIGHT / 5 && profiles[a].getY() <= SCREEN_HEIGHT / 2)         //middle
            {
                profiles[a].setLayoutParams(new RelativeLayout.LayoutParams((int) (82 * d),
                        (int) (123 * d)));

                profileFrames[a].setLayoutParams(new RelativeLayout.LayoutParams((int) (72 * d),
                        (int) (72 * d)));
                profileFrames[a].setX(15);
                profileFrames[a].setY(15);
            } else if (profiles[a].getY() <= SCREEN_HEIGHT / 5)        //top 1/5th
            {
                profiles[a].setLayoutParams(new RelativeLayout.LayoutParams((int) (82 * d * (profiles[a].getY() / (SCREEN_HEIGHT / 5))),
                        (int) (123 * d * (profiles[a].getY() / (SCREEN_HEIGHT / 5)))));

                profileFrames[a].setLayoutParams(new RelativeLayout.LayoutParams((int) (72 * d * (profiles[a].getY() / (SCREEN_HEIGHT / 5))),
                        (int) (72 * d * (profiles[a].getY() / (SCREEN_HEIGHT / 5)))));
                profileFrames[a].setX(15 * (profiles[a].getY() / (SCREEN_HEIGHT / 5)));
                profileFrames[a].setY(15 * (profiles[a].getY() / (SCREEN_HEIGHT / 5)));
            } else        //bottom 1/4th
            {
                profiles[a].setLayoutParams(new RelativeLayout.LayoutParams((int) (82 * d * ((SCREEN_HEIGHT - profiles[a].getY()) / (SCREEN_HEIGHT / 2))),
                        (int) (123 * d * ((SCREEN_HEIGHT - profiles[a].getY()) / (SCREEN_HEIGHT / 2)))));

                profileFrames[a].setLayoutParams(new RelativeLayout.LayoutParams((int) (72 * d * ((SCREEN_HEIGHT - profiles[a].getY()) / (SCREEN_HEIGHT / 2))),
                        (int) (72 * d * ((SCREEN_HEIGHT - profiles[a].getY()) / (SCREEN_HEIGHT / 2)))));
                profileFrames[a].setX(15 * ((SCREEN_HEIGHT - profiles[a].getY()) / (SCREEN_HEIGHT / 2)));
                profileFrames[a].setY(15 * ((SCREEN_HEIGHT - profiles[a].getY()) / (SCREEN_HEIGHT / 2)));
            }
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v.equals(reCenter))
        {
            long currentTime=Calendar.getInstance().getTimeInMillis();
            distance-=100;
            currentTime=Calendar.getInstance().getTimeInMillis();
            if(distance!=0 && currentTime-endTime<=5000)
            {
                started=false;
                startReCenter();
            }
        }
    }
}
