//package mikhailerofeev.com.github.uho.utils;
//
//import android.app.Application;
//import android.content.Context;
//
//import com.google.inject.AbstractModule;
//import com.google.inject.Guice;
//import com.google.inject.Injector;
//
///**
//* @author m-erofeev
//* @since 20.03.15
//*/
//public class InjectorApplication extends Application {
//
//  private static Context context;
//  private static Injector injector;
//
//
//  public static <T> T get(Class<T> type) {
//    return injector.getBinding(type).getProvider().get();
//  }
//
//  public void onCreate() {
//    injector = Guice.createInjector(new ApplicationModule());
//    context = getApplicationContext();
//  }
//
//  private class ApplicationModule extends AbstractModule {
//    @Override
//    protected void configure() {
//      bind(Application.class).toInstance(InjectorApplication.this);
//    }
//  }
//}
//
