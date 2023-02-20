package com.example.hooked;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    @Override
    //this is for when we leave the intent it destroy the activities
    protected void onDestroy(){
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    TextView textView;
    ImageView play, next,previous;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    SeekBar seekBar;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();//Intent mangaaya hai
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        //here we want to set the textview in play song as the current song
        textContent = intent.getStringExtra("currentSong");//by this we get the name of the current song and storing it in the textContent
        textView.setText(textContent);
        //it starts the marquee
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);//by this we getting the position of the our song
        //Similar to URL, URI (Uniform Resource Identifier) is also a string of characters
        // that identifies a resource on the web either by using location, name or both.It allows uniform identification of the resources.
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer= MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        //seekbar change song aage peeche
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //yaha thread se change karaya hai

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition =0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){//agr chal rha hai to
                    play.setImageResource(R.drawable.play);//iamge ko play kardo
                    mediaPlayer.pause();//on click on the image it will pause
                }else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0){
                    position = position -1;//ek gaana peeche ho rha hai
                }else {//agr position zero hogyi
                    position = songs.size() -1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer= MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);//jab previous kare to icon pause ho jaaye
                seekBar.setMax(mediaPlayer.getDuration());
                // for updating the name of the song also
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != songs.size() - 1 ){
                    position = position +1;//ek gaana aage ho rha hai
                }else {//agr position end song tak hogyi
                    position = 0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer= MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);//jab next kare toh icon pause ho jaaye
                seekBar.setMax(mediaPlayer.getDuration());
                // for updating the name of the song also
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
            }
        });




    }
}