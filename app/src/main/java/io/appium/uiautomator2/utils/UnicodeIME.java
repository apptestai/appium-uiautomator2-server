package io.appium.uiautomator2.utils;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

/**
 * Created by kyang on 2018. 4. 11..
 */

public class UnicodeIME extends InputMethodService {
    private static final String TAG = "AppiumUnicodeIME";

    // encodings
    @SuppressWarnings("InjectedReferences")
    private static final Charset M_UTF7 = Charset.forName("x-IMAP-mailbox-name");
    private static final Charset ASCII  = Charset.forName("US-ASCII");

    private static final CharsetDecoder UTF7_DECODER = M_UTF7.newDecoder();


    /**
     * Special character to shift to Modified BASE64 in modified UTF-7.
     */
    private static final char M_UTF7_SHIFT = '&';

    /**
     * Special character to shift back to US-ASCII in modified UTF-7.
     */
    private static final char M_UTF7_UNSHIFT = '-';

    /**
     * Indicates if current UTF-7 state is Modified BASE64 or not.
     */
    private boolean isShifted = false;
    private long metaState = 0;
    private StringBuilder unicodeString = new StringBuilder();

    private int mImeOptions;


    private static UnicodeIME currentUnicodeIME = null;

    public static UnicodeIME getCurrentUnicodeIME() {
        return currentUnicodeIME;
    }

    public boolean commitString(CharSequence value) {
        InputConnection conn = this.getCurrentInputConnection();
        if (conn == null) {
            return false;
        }
        return conn.commitText(value, 1);
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting) {
        Logger.debug(TAG + ": onStartInput");
//        Log.i(TAG, "onStartInput");
        super.onStartInput(attribute, restarting);

        if (!restarting) {
            metaState = 0;
            isShifted = false;
        }
        unicodeString = null;
        mImeOptions = attribute.imeOptions;
        currentUnicodeIME = this;
    }

    @Override
    public void onFinishInput() {
        Logger.debug(TAG + ": onFinishInput");
        super.onFinishInput();
        unicodeString = null;
        currentUnicodeIME = null;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public boolean onEvaluateInputViewShown() {
        return super.onEvaluateInputViewShown();
//        return false;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Logger.debug(TAG + "onKeyDown (keyCode='" + keyCode + "', event.keyCode='" + event.getKeyCode() + "', metaState='" + event.getMetaState() + "')");

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            return true;
        }

        int c = getUnicodeChar(keyCode, event);

        if (c == 0) {
            return super.onKeyDown(keyCode, event);
        }

        if (!isShifted) {
            if (c == M_UTF7_SHIFT) {
                shift();
                return true;
            } else if (isAsciiPrintable(c)) {
                commitChar(c);
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            if (c == M_UTF7_UNSHIFT) {
                unshift();
            } else {
                appendChar(c);
            }
            return true;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Logger.debug(TAG + "onKeyUp (keyCode='" + keyCode + "', event.keyCode='" + event.getKeyCode() + "', metaState='" + event.getMetaState() + "')");
        metaState = MetaKeyKeyListener.handleKeyUp(metaState, keyCode, event);

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.i(TAG, "keyCode 66 - performEditorAction");
            InputConnection ic = getCurrentInputConnection();
            boolean success = false;
            switch(mImeOptions & (EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
                case EditorInfo.IME_ACTION_GO:
                    success = ic.performEditorAction(EditorInfo.IME_ACTION_GO);
                    Log.i(TAG, "ic.performEditorAction(EditorInfo.IME_ACTION_GO)");
                    break;
                case EditorInfo.IME_ACTION_NEXT:
                    success = ic.performEditorAction(EditorInfo.IME_ACTION_NEXT);
                    Log.i(TAG, "ic.performEditorAction(EditorInfo.IME_ACTION_NEXT)");
                    break;
                case EditorInfo.IME_ACTION_PREVIOUS:
                    success = ic.performEditorAction(EditorInfo.IME_ACTION_PREVIOUS);
                    Log.i(TAG, "ic.performEditorAction(EditorInfo.IME_ACTION_PREVIOUS)");
                    break;
                case EditorInfo.IME_ACTION_SEARCH:
                    success = ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH);
                    Log.i(TAG, "ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH)");
                    break;
                case EditorInfo.IME_ACTION_SEND:
                    success = ic.performEditorAction(EditorInfo.IME_ACTION_SEND);
                    Log.i(TAG, "ic.performEditorAction(EditorInfo.IME_ACTION_SEND)");
                    break;
                case EditorInfo.IME_ACTION_DONE:
                    success = ic.performEditorAction(EditorInfo.IME_ACTION_DONE);
                    Log.i(TAG, "ic.performEditorAction(EditorInfo.IME_ACTION_DONE)");
                    break;
                default:
                    success = ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    Log.i(TAG, "ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))");
                    break;
            }
            return success;
        }

        return super.onKeyUp(keyCode, event);
    }

    private void shift() {
        isShifted = true;
        unicodeString = new StringBuilder();
        appendChar(M_UTF7_SHIFT);
    }

    private void unshift() {
        isShifted = false;
        unicodeString.append(M_UTF7_UNSHIFT);
        String decoded = decodeUtf7(unicodeString.toString());
        getCurrentInputConnection().commitText(decoded, 1);
        unicodeString = null;
    }

    private int getUnicodeChar(int keyCode, KeyEvent event) {
        metaState = MetaKeyKeyListener.handleKeyDown(metaState, keyCode, event);
        int c = event.getUnicodeChar(event.getMetaState());
        metaState = MetaKeyKeyListener.adjustMetaAfterKeypress(metaState);
        return c;
    }

    private void commitChar(int c) {
        getCurrentInputConnection().commitText(String.valueOf((char) c), 1);
    }

    private void appendChar(int c) {
        unicodeString.append((char) c);
    }

    private String decodeUtf7(String encStr) {
        ByteBuffer encoded = ByteBuffer.wrap(encStr.getBytes(ASCII));
        String decoded;
        try {
            CharBuffer buf = UTF7_DECODER.decode(encoded);
            decoded = buf.toString();
        } catch (CharacterCodingException e) {
            decoded = encStr;
        }
        return decoded;
    }

    private boolean isAsciiPrintable(int c) {
        return c >= 0x20 && c <= 0x7E;
    }
}
