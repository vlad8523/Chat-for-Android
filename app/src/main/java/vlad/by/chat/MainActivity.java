package vlad.by.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_chat;
    private Button send;
    private EditText message;

    private static String mess;

    private Client clientOnPhone;

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
        this.mess = mess;
        runOnUiThread(action);
    }
    //Действие для изменения тектса
    Runnable action = new Runnable() {
        @Override
        public void run() {
            StringBuilder chat = new StringBuilder(tv_chat.getText());
            chat.append("\n");
            chat.append(mess);
            tv_chat.setText(chat.toString());
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
