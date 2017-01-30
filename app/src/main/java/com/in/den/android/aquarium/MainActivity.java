package com.in.den.android.aquarium;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private ScheduledFuture future;
    private ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private Observable<Integer> motionEventObservable;
    private static float mScreenWidth;
    private static float mScreenHeight;
    private PlayFieldLayout playFieldLayout;
    private BasketLayout basketLayout;
    private ArrayMap<String,Animal> animalArrayMap;
    private static final String GREENFISH = "greenfish";
    private static final String YELLOWFISH = "yellowfish";
    private static final String DRAGON = "dragon";
    private Animal dragon;
    private Animal fish;
    private Animal fish2;
    private static final float initialXSpeed = (float)0.8;
    private static final float initailYSpeed = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playFieldLayout = new PlayFieldLayout(MainActivity.this);
        setContentView(playFieldLayout);

        init();

        initObservable();

        startTimer();
    }

    private void init() {

        DisplayMetrics metrics = new DisplayMetrics();

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                .getDefaultDisplay();

        display.getMetrics(metrics);

        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        playFieldLayout.setBackgroundColor(Color.DKGRAY);

        basketLayout = new BasketLayout(MainActivity.this);
        basketLayout.setX(mScreenWidth / 3 );
        basketLayout.setY(mScreenHeight / 3 * 2);
        basketLayout.setBackgroundColor(Color.GRAY);
        basketLayout.setLayoutParams(new FrameLayout.LayoutParams(500, 500));


        dragon = new Animal(MainActivity.this, 150, 100, (float)0.2, (float)0.3, R.drawable.dragon);
        fish = new Animal(MainActivity.this, 200, 300, (float)0.8, (float)1, R.drawable.sakana_green);
        fish2 = new Animal(MainActivity.this, 0, 800, (float)0.8, (float)0.5, R.drawable.sakana_yellow);
        animalArrayMap = new ArrayMap<String, Animal>();
        animalArrayMap.put(DRAGON,dragon);
        animalArrayMap.put(GREENFISH,fish);
        animalArrayMap.put(YELLOWFISH, fish2);

        basketLayout.addView(dragon);
        playFieldLayout.addView(basketLayout);
        playFieldLayout.addView(fish);
        playFieldLayout.addView(fish2);
    }

    private void initObservable() {

        motionEventObservable = Observable.
                create(new Observable.OnSubscribe<Integer>() {
                    @Override
                    public void call(final Subscriber<? super Integer> subscriber) {
                        playFieldLayout.setOnTouchListener(
                                new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {

                                        if (event.getAction() == MotionEvent.ACTION_DOWN) {

                                            subscriber.onNext(new Integer(1));
                                        }

                                     return true;
                                    }
                                });
                    }
                });

        // nb clicks occured in 1 secs
        Observable<Integer> bufferobservable =
                motionEventObservable.buffer(1000, TimeUnit.MILLISECONDS).
                        map(
                                new Func1<List<Integer>, Integer>() {
                                    @Override
                                    public Integer call(List<Integer> integers) {
                                        return integers.size();
                                    }
                                }
                        );

        bufferobservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {

                if (integer != null && integer == 1) {
                    if (animalArrayMap.get(GREENFISH).getVisibility() == ImageView.VISIBLE) {
                        animalArrayMap.get(GREENFISH).setVisibility(ImageView.GONE);
                    } else {
                        animalArrayMap.get(GREENFISH).setVisibility(ImageView.VISIBLE);
                    }

                } else if (integer != null && integer == 2) {
                    if (animalArrayMap.get(GREENFISH).getXSpeed() == initialXSpeed) {
                        animalArrayMap.get(GREENFISH).changeSpeed(initialXSpeed * 4, initailYSpeed * 6);
                    } else {
                        animalArrayMap.get(GREENFISH).changeSpeed(initialXSpeed, initailYSpeed);
                    }
                }
                else if(integer != null && integer >=3) {
                    Toast.makeText(MainActivity.this, "Lot of clicks!", Toast.LENGTH_LONG).show();
                    /*
                    code below blocks the scheduler working correctly...
                    int color = ((ColorDrawable)playFieldLayout.getBackground()).getColor();

                    if(color == Color.BLACK)
                        playFieldLayout.setBackgroundColor(Color.DKGRAY);
                    else
                        playFieldLayout.setBackgroundColor(Color.BLACK);
                        */
                }
            }
        });

    }

    private void updateAnimals() {

        Iterator<Animal> animalite = animalArrayMap.values().iterator();

        while(animalite.hasNext()) {
            animalite.next().updatePosition();
        }
    }

    public void canselTimer() {
        future.cancel(true);
    }

    public void startTimer() {
        future =
                scheduler.scheduleAtFixedRate(
                        new Runnable() {
                            @Override
                            public void run() {
                                updateAnimals();
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                playFieldLayout.invalidate();
                                            }
                                        }
                                );
                            }
                        }
                        , 0, 10, TimeUnit.MILLISECONDS);
    }

}
