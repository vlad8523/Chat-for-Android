package vlad.by.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView tv_chat;
    private Button send;
    private EditText message;
    private Context thisActivity = this;

    private static String textMess;
    private static String textToast;

    private Client clientOnPhone;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Intent settingsActivity = new Intent(this, SettingsActivity.class);
                startActivity(settingsActivity);
                break;
            case R.id.action_repeat_connections:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        clientOnPhone = new Client(this);

        tv_chat = (TextView)findViewById(R.id.TVChat);
        send = (Button) findViewById(R.id.BTN_Send);
        message = (EditText) findViewById(R.id.ET_Message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().equals("")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientOnPhone.sendMessage(message.getText().toString());

                        }
                    }).start();
                    message.setText("");
                }
            }
        });
    }
    //Метод для обращения к изменению текста из вне основного потока
    public void changeChat(String mess){
        textMess = mess;
        runOnUiThread(actionChangeChat);
    }
    //Действие для изменения тектса
    Runnable actionChangeChat = new Runnable() {
        @Override
        public void run() {
            StringBuilder chat = new StringBuilder(tv_chat.getText());
            chat.append("\n");
            chat.append(textMess);
            tv_chat.setText(chat.toString());
        }
    };
    public void showToast(String text){
        textToast = text;
        runOnUiThread(actionShowToast);
    }
    Runnable actionShowToast = new Runnable() {
        @Override
        public void run() {
              Toast.makeText(thisActivity,textToast,Toast.LENGTH_SHORT).show();
        }
    };
//    @Override
//    protected void onPause() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                clientOnPhone.closeAllConnections();
//            }
//        }).start();
//        super.onPause();
//    }
    //При закрытии закрывает все соединеия
    @Override
    protected void onStop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clientOnPhone.closeAllConnections();
            }
        }).start();
        super.onStop();
    }
}
