package learn.shendy.ebookshop.utils;

import androidx.databinding.InverseMethod;

public class BindingConverters {
    @InverseMethod("fromPrice")
    public static Double toPrice(String text) {
        return Double.valueOf(text);
    }

    public static String fromPrice(Double price) {
        return price == 0f ? "" : String.valueOf(price);
    }
}
