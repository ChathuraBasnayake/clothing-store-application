import com.google.inject.Guice;
import com.google.inject.Injector;
import com.icet.clothify.config.AppModule;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Starter extends Application {

    public static void main(String[] args) {

        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {

        Injector inject = Guice.createInjector(new AppModule());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Authentication_form.fxml"));
        loader.setControllerFactory(inject::getInstance);

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Clothify");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/icon/clothify.ico"))); // adjust path as needed

        stage.show();


    }
}