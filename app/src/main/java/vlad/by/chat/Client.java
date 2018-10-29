package vlad.by.chat;

import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable {
    private static Socket connection = null;
    private static Scanner input;
    private static PrintWriter output;

    private static boolean isOpen = true;

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
            output = new PrintWriter(connection.getOutputStream());
            input = new Scanner(connection.getInputStream());
            checkInput(input);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //Открывает соединение и отдает Socket
    private static Socket openConnection(){
        closeConnection();
        try {
            connection = new Socket(Const.HOST,Const.PORT);
            output = new PrintWriter(connection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }
    //Закрывает соединение если оно было открыто
    private static void closeConnection() {
        if (connection!=null){
            if (!connection.isClosed()){
                try {
                    connection.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        connection = null;
    }
    //Просмотривает ввод на предмет сообщений
    private void checkInput(Scanner input){
        while (isOpen) {
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
        isOpen = false;
        if (input!=null)input.close();
        try {
            if (connection!=null) connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
