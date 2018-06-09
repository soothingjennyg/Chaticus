import io.javalin.Javalin;

public class HelloWorld {
    public static void main(String[] args) {
        Javalin app = Javalin.start(7000);
        app.get("/", ctx -> ctx.result("Hello Jenn You Can Do this you are the best"));
        app.get("/jenn", ctx -> ctx.result("Hello You Are Rocking This! "));
    }
}