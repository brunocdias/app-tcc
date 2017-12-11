package com.google.sample.cloudvision;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.util.ArrayList;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Locale;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class ActSegundaTela extends AppCompatActivity  implements TextToSpeech.OnInitListener {

    private static final String API_KEY = "AIzaSyB1DITmOdgzd8L2pC4MI1hoHFm7OCrtbac";

    private TextToSpeech tts;
    private ImageButton speaker;
    private TextView txtTarget;
    private TextView txtSource;
    private Button btnResultado;
    private TextView txtFala01;
    private TextView txtFala02;
    private TextView txtFala03;
    private ImageButton btnSpeak01;
    private ImageButton btnSpeak02;
    private ImageButton btnSpeak03;
    int tentativa = 1; //primeira segunda terceira
    int qtd = 0; //qtd de acertos
    boolean botao1=false, botao2=false, botao3=false;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_segunda_tela);

        tts = new TextToSpeech(this, this);
        speaker = (ImageButton) findViewById(R.id.speaker);
        txtTarget = (TextView) findViewById(R.id.txtTarget);
        txtSource = (TextView) findViewById(R.id.txtSource);
        btnResultado = (Button) findViewById(R.id.btnResultado);
        txtFala01 = (TextView) findViewById(R.id.txtFala01);
        txtFala02 = (TextView) findViewById(R.id.txtFala02);
        txtFala03 = (TextView) findViewById(R.id.txtFala03);
        btnSpeak01 = (ImageButton) findViewById(R.id.btnSpeak01);
        btnSpeak02 = (ImageButton) findViewById(R.id.btnSpeak02);
        btnSpeak03 = (ImageButton) findViewById(R.id.btnSpeak03);

        Bundle bundle = getIntent().getExtras();
        String word = "";
        if(bundle.containsKey("Name")){
            word = (String) bundle.getString("Name");
            txtTarget.setText(word);
        }

        final String finalWord = word;
        btnResultado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                btnResultado.setEnabled(false);
                txtSource.setText("Traduzindo, aguarde...");

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {

                        Translate translate = TranslateOptions.newBuilder().setApiKey(API_KEY).build().getService();

                        final Translation translation = translate.translate(finalWord, Translate.TranslateOption.targetLanguage("pt"));

                        txtSource.post(new Runnable() {
                            @Override
                            public void run() {
                                txtSource.setText(translation.getTranslatedText());
                            }
                        });
                        return null;
                    }
                }.execute();
            }
        });

        attempt(tentativa);

        speaker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                speakOut();
            }

        });
    }

    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speaker.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        String text = txtTarget.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    //metodo pra intercalar os botoes de speech
    private void attempt(int t) {

        tentativa = t;

        if (tentativa == 1) {
            txtFala01.setText("");
            txtFala02.setText("");
            txtFala03.setText("");

            btnSpeak01.setEnabled(true);
            btnSpeak01.setBackgroundResource(R.drawable.ico_mic);
            btnSpeak02.setEnabled(false);
            btnSpeak02.setBackgroundResource(R.drawable.mic_desativado);
            btnSpeak03.setEnabled(false);
            btnSpeak03.setBackgroundResource(R.drawable.mic_desativado);

            btnSpeak01.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    botao1 = true;
                    promptSpeechInput();
                }
            });
        }else

            if (tentativa == 2) {

                btnSpeak02.setEnabled(true);
                btnSpeak02.setBackgroundResource(R.drawable.ico_mic);
                btnSpeak01.setEnabled(false);
                btnSpeak01.setBackgroundResource(R.drawable.mic_desativado);
                btnSpeak03.setEnabled(false);
                btnSpeak03.setBackgroundResource(R.drawable.mic_desativado);

                btnSpeak02.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        botao2 = true;
                        promptSpeechInput();
                    }
                });
            }else
                if(tentativa == 3){

                        btnSpeak03.setEnabled(true);
                        btnSpeak03.setBackgroundResource(R.drawable.ico_mic);
                        btnSpeak01.setEnabled(false);
                        btnSpeak01.setBackgroundResource(R.drawable.mic_desativado);
                        btnSpeak02.setEnabled(false);
                        btnSpeak02.setBackgroundResource(R.drawable.mic_desativado);

                        btnSpeak03.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                botao3=true;
                                promptSpeechInput();
                            }
                        });
                }


    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String target = txtTarget.getText().toString();

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(botao1){
                        txtFala01.setText(result.get(0));
                        botao1=false;
                        if(target.equals(result.get(0))){
                            txtFala01.setTextColor(Color.GREEN);
                            AlertDialog alertDialog;
                            alertDialog = new AlertDialog.Builder(this).create();
                            alertDialog.setTitle("Medalha de bronze!");
                            alertDialog.setMessage("Parabéns, continue treinando.");
                            alertDialog.setIcon(R.drawable.bronze);
                            alertDialog.show();
                            qtd ++;
                            attempt(2);
                        }else
                            {
                                txtFala01.setTextColor(Color.RED);
                                AlertDialog alertDialog;
                                alertDialog = new AlertDialog.Builder(this).create();
                                alertDialog.setTitle("Você errou.");
                                alertDialog.setMessage("Não desista, continue treinando.");
                                alertDialog.setIcon(R.drawable.emoji_triste);
                                alertDialog.show();
                                attempt(2);
                            }
                    }
                    else
                        if(botao2){
                            txtFala02.setText(result.get(0));
                            botao2 = false;
                            if(target.equals(result.get(0))){
                                switch (qtd) {
                                    case 0:
                                        txtFala02.setTextColor(Color.GREEN);
                                        AlertDialog alertDialog1;
                                        alertDialog1 = new AlertDialog.Builder(this).create();
                                        alertDialog1.setTitle("Medalha de bronze!");
                                        alertDialog1.setMessage("Parabéns, continue treinando.");
                                        alertDialog1.setIcon(R.drawable.bronze);
                                        alertDialog1.show();
                                        qtd ++;
                                        attempt(3);
                                    case 1:
                                        txtFala02.setTextColor(Color.GREEN);
                                        AlertDialog alertDialog2;
                                        alertDialog2 = new AlertDialog.Builder(this).create();
                                        alertDialog2.setTitle("Medalha de prata!");
                                        alertDialog2.setMessage("Parabéns, continue treinando.");
                                        alertDialog2.setIcon(R.drawable.prata);
                                        alertDialog2.show();
                                        qtd ++;
                                        attempt(3);
                                }
                            } else
                                {
                                    txtFala02.setTextColor(Color.RED);
                                    AlertDialog alertDialog;
                                    alertDialog = new AlertDialog.Builder(this).create();
                                    alertDialog.setTitle("Você errou.");
                                    alertDialog.setMessage("Não desista, continue treinando.");
                                    alertDialog.setIcon(R.drawable.emoji_triste);
                                    alertDialog.show();
                                    attempt(3);
                                }
                        }
                        else
                            if(botao3){
                                txtFala03.setText(result.get(0));
                                botao3 = false;
                                if(target.equals(result.get(0))){
                                    switch (qtd) {
                                        case 0:
                                            txtFala03.setTextColor(Color.GREEN);
                                            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(this);
                                            alertDialog1.setTitle("Medalha de bronze!");
                                            alertDialog1.setMessage("Parabéns, deseja continuar treinando?");
                                            alertDialog1.setIcon(R.drawable.bronze);
                                            alertDialog1.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    //primeira tela
                                                    Intent it = new Intent(ActSegundaTela.this, MainActivity.class);
                                                    startActivity(it);
                                                }
                                            });
                                            alertDialog1.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    attempt(1);
                                                }
                                            });
                                            alertDialog1.create();
                                            alertDialog1.show();
                                            btnSpeak03.setEnabled(false);
                                            btnSpeak03.setBackgroundResource(R.drawable.mic_desativado);
                                            break;
                                        case 1:
                                            txtFala03.setTextColor(Color.GREEN);
                                            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
                                            alertDialog2.setTitle("Medalha de prata!");
                                            alertDialog2.setMessage("Parabéns, você quase acertou tudo, dejesa continuar treinando?");
                                            alertDialog2.setIcon(R.drawable.prata);
                                            alertDialog2.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    //primeira tela
                                                    Intent it = new Intent(ActSegundaTela.this, MainActivity.class);
                                                    startActivity(it);
                                                }
                                            });
                                            alertDialog2.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    attempt(1);
                                                }
                                            });
                                            alertDialog2.create();
                                            alertDialog2.show();
                                            btnSpeak03.setEnabled(false);
                                            btnSpeak03.setBackgroundResource(R.drawable.mic_desativado);
                                            break;
                                        case 2:
                                            txtFala03.setTextColor(Color.GREEN);
                                            AlertDialog.Builder alertDialog3 = new AlertDialog.Builder(this);
                                            alertDialog3.setTitle("Medalha de ouro!");
                                            alertDialog3.setMessage("Parabéns, você acertou todas." +
                                                    "Deseja pesquisar uma nova imagem?");
                                            alertDialog3.setIcon(R.drawable.ouro);
                                            alertDialog3.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {

                                                }
                                            });
                                            alertDialog3.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    //primeira tela
                                                    Intent it = new Intent(ActSegundaTela.this, MainActivity.class);
                                                    startActivity(it);
                                                }
                                            });
                                            alertDialog3.create();
                                            alertDialog3.show();
                                            btnSpeak03.setEnabled(false);
                                            btnSpeak03.setBackgroundResource(R.drawable.mic_desativado);
                                            break;
                                    }
                                } else
                                    {
                                        txtFala03.setTextColor(Color.RED);
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                                        alertDialog.setTitle("Você errou todas as pronuncias.");
                                        alertDialog.setMessage("Deseja refazer?");
                                        alertDialog.setIcon(R.drawable.emoji_triste);
                                        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                //primeira tela
                                                Intent it = new Intent(ActSegundaTela.this, MainActivity.class);
                                                startActivity(it);
                                            }
                                        });
                                        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                attempt(1);
                                            }
                                        });
                                        alertDialog.create();
                                        alertDialog.show();
                                        btnSpeak03.setEnabled(false);
                                        btnSpeak03.setBackgroundResource(R.drawable.mic_desativado);
                                    }
                            }
                }
                break;
            }

        }
    }

}
