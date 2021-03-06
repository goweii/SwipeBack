package per.goweii.swipeback;

import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface SwipeBackTransformer {
    void initialize(
            @NonNull View currentView,
            @Nullable View previousView
    );

    void transform(
            @NonNull View currentView,
            @Nullable View previousView,
            @FloatRange(from = 0.0, to = 1.0) float fraction,
            @NonNull SwipeBackDirection swipeDirection
    );

    void restore(
            @NonNull View currentView,
            @Nullable View previousView
    );
}