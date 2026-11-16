package com.example.cargalote;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

public class EstadoUtil {

    public static int color(Context c, EstadoOperacion estado) {
        return ContextCompat.getColor(c, estado.colorRes);
    }

    /** Muestra el estado como etiqueta redondeada con el color correspondiente. */
    public static void aplicarBadge(TextView tv, EstadoOperacion estado) {
        int color = color(tv.getContext(), estado);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setCornerRadius(tv.getResources().getDisplayMetrics().density * 12);
        fondo.setColor(ColorUtils.setAlphaComponent(color, 50));

        tv.setBackground(fondo);
        tv.setTextColor(color);
        tv.setText(estado.etiqueta);
    }
}