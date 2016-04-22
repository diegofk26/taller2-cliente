package com.example.sebastian.tindertp.animationTools;

import android.support.v4.view.ViewPager;
import android.view.View;
//! Implementa la animacion de solapado de imagenes.
/** La pagina de atras con un grado de transparencia y escalada
*  y la de adelante normal.*/
public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

     //!Posición de la página relativa a la posición actual frente-y-centro del ViewPager.
    /** 0 = frente y centro. 1 la paigna se va a la derecha, y
     * -1 la pagina se va a la izquierda.
     */
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // Se va a la izquierda.
            view.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Utiliza la transición de imagen por defecto cuando se mueve a la página de la izquierda
            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            // fade out.
            view.setAlpha(1 - position);

            // Contrarrestar la transición de imagen predeterminada
            view.setTranslationX(pageWidth * -position);

            // Escalar la página de abajo (entre MIN_SCALE y 1)
            float scaleFactor = MIN_SCALE
                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // Se va a la derecha.
            view.setAlpha(0);
        }
    }
}