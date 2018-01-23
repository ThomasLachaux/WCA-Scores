package com.adrastel.niviel.assets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.Spanned;

public class DetailsMaker {

    private Context context;
    private String message = "";

    public DetailsMaker(Context context) {
        this.context = context;
    }

    public DetailsMaker add(@StringRes int titleRes, String body) {

        String title = context.getString(titleRes);

        return add(title, body);
    }

    @SuppressWarnings("SameParameterValue")
    public DetailsMaker add(@StringRes int titleRes, @StringRes int bodyRes) {
        String title = context.getString(titleRes);
        String body = context.getString(bodyRes);

        return add(title, body);
    }

    public DetailsMaker add(String title, String body) {

        String message = "<strong>" + title + " : </strong>" + body + "<br/>";

        this.message += message;

        return this;
    }

    public DetailsMaker add(String body) {
        String message = body + "<br/>";

        this.message += message;

        return this;
    }

    public DetailsMaker br() {
        this.message += "<br/>";

        return this;
    }

    public Spanned build() {
        return Assets.fromHtml(message);
    }

}
