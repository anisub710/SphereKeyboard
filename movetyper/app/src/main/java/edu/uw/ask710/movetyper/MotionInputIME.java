package edu.uw.ask710.movetyper;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.view.View;

/**
 * Created by Anirudh Subramanyam on 11/23/2017.
 */

public class MotionInputIME extends InputMethodService {

    @Override
    public View onCreateInputView() {
        KeyboardView inputView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        return inputView;
    }
}
