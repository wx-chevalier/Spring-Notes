package wx.application.daemon;

import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wx.application.utils.EnvironmentUtils;

@Slf4j
@Component
public class Daemons implements Daemon {

  private EnvironmentUtils environmentUtils;

  @Autowired
  public void setEnvironmentUtils(EnvironmentUtils environmentUtils) {
    this.environmentUtils = environmentUtils;
  }

  private List<Daemon> daemons;

  private CompletableObserver errObserver =
      new CompletableObserver() {
        @Override
        public void onSubscribe(Disposable d) {}

        @Override
        public void onComplete() {}

        @Override
        public void onError(Throwable e) {
          log.error(e.getMessage(), e);
        }
      };

  public Daemons() {
    daemons = new ArrayList<>();
  }

  @PostConstruct
  @Override
  public void start() {
    daemons.forEach(Daemon::start);

    environmentUtils.isTestOrDevEnv();
  }

  @PreDestroy
  @Override
  public void cleanup() {
    daemons.forEach(Daemon::cleanup);
  }
}
