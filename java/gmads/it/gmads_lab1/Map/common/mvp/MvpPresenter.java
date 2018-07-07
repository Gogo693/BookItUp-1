package gmads.it.gmads_lab1.Map.common.mvp;

public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);
    void detachView();
    boolean isViewAttached();
}
