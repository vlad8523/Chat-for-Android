package vlad.by.chat;

import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable {
    //Socket соединения
    private static Socket connection = null;
    //Ввод-вывод
    private static Scanner input;
    private static PrintWriter output;
    //Открыто ли соединение
    private static boolean isConnectionOpen = false;

    private MainActivity m;

    public Client(MainActivity mainActivity){
        m = mainActivity;
        new Thread(this).start();
    }

    private static String getName(){return Const.NAME;}
    //Поток для соединения с сервером
    @Override
    public void run() {
        try{
            connection = openConnection();
            if (connection!=null) {
                if (connection.isConnected()) {
                    output = new PrintWriter(connection.getOutputStream());
                    input = new Scanner(connection.getInputStream());
                }
            }
            else m.showToast("Ошибка соединения");
            checkInput(input);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Открывает соединение и отдает Socket
    private Socket openConnection(){
        closeConnection();
        try {
            connection = new Socket(Const.HOST,Const.PORT);
            output = new PrintWriter(connection.getOutputStream());
            isConnectionOpen = connection.isConnected();
        } catch (IOException e) {
            isConnectionOpen = false;
        }
        return connection;
    }
    //Закрывает соединение если оно было открыто
    private static void closeConnection() {
        if (connection!=null){
            if (!connection.isClosed()){
                try {
                    connection.close();
                    isConnectionOpen = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        connection = null;
    }
    //Просмотривает ввод на предмет сообщений
    private void checkInput(Scanner input){
        while (isConnectionOpen) {
            if (input.hasNext()){
                String inMess = input.nextLine();
                m.changeChat(inMess);
            }
        }
    }
    //Метод для отправки сообщений в чат
    public void sendMessage(String text){
        if (connection!=null){
            output.println(getName()+": "+ text);
            output.flush();
        }
    }

    //Метод для закрытия всех соединений и отправляет сообщение о том, что нужно закрыть поток с чтением
    public void closeAllConnections(){
        if (output!=null){
            output.println(getName()+" выш(ел)/(ла) из чата");
            output.println("##session##end##");
            output.flush();
            output.close();
        }
        isConnectionOpen = false;
        if (input!=null)input.close();
        try {
            if (connection!=null) connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
