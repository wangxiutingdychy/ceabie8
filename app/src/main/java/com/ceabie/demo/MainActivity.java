package com.ceabie.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

import bolts.CancellationTokenSource;
import bolts.Continuation;
import bolts.Task;
import demo.ceabie.com.demo.R;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends AppCompatActivity {

    private TextView mViewById;
    private long mMillis;
    ArrayList<CancellationTokenSource> tokenSources = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewById = (TextView) findViewById(R.id.text);

        Random random = new Random();

        for (int i=0; i<20; i++) {
            final CancellationTokenSource tokenSource = new CancellationTokenSource();
            tokenSources.add(tokenSource);

            final int mi = i;
            Task<Void> delay = Task.delay(4000 + random.nextInt(2000));

            delay.continueWith(new Continuation<Void, Void>() {
                @Override
                public Void then(Task<Void> task) throws Exception {
                    synchronized (tokenSources) {
                        tokenSources.remove(tokenSource);
                    }

                    if (!tokenSource.isCancellationRequested()) {
                        tokenSource.cancel();

                        log("run a task: " + mi);
                    }

                    return null;
                }
            }, Task.BACKGROUND_EXECUTOR, tokenSource.getToken());
        }

        findViewById(R.id.btn_save_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancellationTokenSource tokenSource;
                int index;
                synchronized (tokenSources) {
                    index = new Random().nextInt(tokenSources.size());
                    tokenSource = tokenSources.get(index);

                    tokenSources.remove(index);
                }

                if (!tokenSource.isCancellationRequested()) {
                    tokenSource.cancel();
                }

                log("cancel: [" + index + "]");

//                int dfg = 121211;
//                Observable.just(1, 2, dfg)
//                        .map(new Func1<Integer, Integer>() {
//                            @Override
//                            public Integer call(Integer integer) {
//                                return integer + 10;
//                            }
//                        })
////                        .flatMap(new Func1<Integer, Observable<Integer>>() {
////                            @Override
////                            public Observable<Integer> call(Integer integer) {
////                                return Observable.just(integer + 10);
////                            }
////                        })
////                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Action1<Integer>() {
//                            @Override
//                            public void call(Integer integer) {
//                                log(String.valueOf(integer));
//                            }
//                        });
            }
        });
    }


    private void log(final String stext) {
        Task.call(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                mViewById.setText(mViewById.getText().toString() + "\n" + stext);
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);

    }
}
