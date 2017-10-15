package com.google.sample.cloudvision;

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


public class ActSegundaTela extends AppCompatActivity {

    private static final String API_KEY = "";

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
            //envia para o translate api para traduzir a palavra pro portugues
        }

        final String finalWord = word;
        btnResultado.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
    }

    //metodo pra intercalar os botoes de speech
    private void attempt(int t) {

        tentativa = t;

        if (tentativa == 1) {
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
                                            AlertDialog alertDialog1;
                                            alertDialog1 = new AlertDialog.Builder(this).create();
                                            alertDialog1.setTitle("Medalha de bronze!");
                                            alertDialog1.setMessage("Parabéns, continue treinando.");
                                            alertDialog1.setIcon(R.drawable.bronze);
                                            alertDialog1.show();
                                            qtd ++;
                                            break;
                                        case 1:
                                            txtFala03.setTextColor(Color.GREEN);
                                            AlertDialog alertDialog2;
                                            alertDialog2 = new AlertDialog.Builder(this).create();
                                            alertDialog2.setTitle("Medalha de prata!");
                                            alertDialog2.setMessage("Parabéns, continue treinando.");
                                            alertDialog2.setIcon(R.drawable.prata);
                                            alertDialog2.show();
                                            qtd ++;
                                            break;
                                        case 2:
                                            txtFala03.setTextColor(Color.GREEN);
                                            AlertDialog alertDialog3;
                                            alertDialog3 = new AlertDialog.Builder(this).create();
                                            alertDialog3.setTitle("Medalha de ouro!");
                                            alertDialog3.setMessage("Parabéns, você acertou todas.");
                                            alertDialog3.setIcon(R.drawable.prata);
                                            alertDialog3.show();
                                            qtd ++;
                                            break;
                                    }
                                } else
                                    {
                                        txtFala03.setTextColor(Color.RED);
                                        AlertDialog alertDialog;
                                        alertDialog = new AlertDialog.Builder(this).create();
                                        alertDialog.setTitle("Você errou.");
                                        alertDialog.setMessage("Não desista, continue treinando.");
                                        alertDialog.setIcon(R.drawable.emoji_triste);
                                        alertDialog.show();
                                        attempt(3);
                                    }
                            }
                }
                break;
            }

        }
    }

}
