package se.fulkopinglibraryweb.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import se.fulkopinglibraryweb.config.UnifiedAppConfig;

@WebListener
public class SpringContextListener implements ServletContextListener {
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(UnifiedAppConfig.class);
        context.refresh();
        sce.getServletContext().setAttribute("springContext", context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext)
                sce.getServletContext().getAttribute("springContext");
        if (context != null) {
            context.close();
        }
    }
}
