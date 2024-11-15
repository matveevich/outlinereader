import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

public class FTPESUploader {
    public static void main(String[] args) {
        String server = "ftps.example.com";
        int port = 21; // Порт для Explicit FTPS
        String user = "your_username";
        String password = "your_password";
        String localFilePath = "C:/path/to/your/local/file.txt";
        String remoteFilePath = "/path/on/server/file.txt";

        Socket socket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {
            // Подключаемся к серверу по обычному FTP
            socket = new Socket(server, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Читаем приветствие
            System.out.println("Ответ сервера: " + reader.readLine());

            // Отправляем команду AUTH TLS для перехода в защищённый режим
            writer.println("AUTH TLS");
            System.out.println("Ответ сервера: " + reader.readLine());

            // Создаём защищённый SSL-сокет поверх существующего подключения
            SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslSocket = (SSLSocket) sslFactory.createSocket(socket, server, port, true);
            sslSocket.startHandshake();

            // Обновляем потоки для работы через SSL
            reader = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
            writer = new PrintWriter(sslSocket.getOutputStream(), true);

            // Авторизация
            writer.println("USER " + user);
            System.out.println("Ответ сервера: " + reader.readLine());
            writer.println("PASS " + password);
            System.out.println("Ответ сервера: " + reader.readLine());

            // Устанавливаем режим передачи данных (бинарный)
            writer.println("TYPE I");
            System.out.println("Ответ сервера: " + reader.readLine());

            // Переходим в пассивный режим
            writer.println("PASV");
            String response = reader.readLine();
            System.out.println("Ответ сервера: " + response);

            // Парсим адрес и порт из ответа сервера
            String[] parts = response.split("[()]")[1].split(",");
            String dataHost = parts[0] + "." + parts[1] + "." + parts[2] + "." + parts[3];
            int dataPort = (Integer.parseInt(parts[4]) << 8) + Integer.parseInt(parts[5]);

            // Подключаемся к порту для передачи данных
            Socket dataSocket = new Socket(dataHost, dataPort);

            // Отправляем команду загрузки файла
            writer.println("STOR " + remoteFilePath);
            System.out.println("Ответ сервера: " + reader.readLine());

            // Передаём файл
            try (OutputStream dataOut = dataSocket.getOutputStream();
                 FileInputStream fileInput = new FileInputStream(localFilePath)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInput.read(buffer)) != -1) {
                    dataOut.write(buffer, 0, bytesRead);
                }
                dataOut.flush();
                System.out.println("Файл успешно загружен.");
            }

            dataSocket.close();
            System.out.println("Ответ сервера: " + reader.readLine());

            // Завершаем сессию
            writer.println("QUIT");
            System.out.println("Ответ сервера: " + reader.readLine());

        } catch (IOException e) {
            System.out.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
