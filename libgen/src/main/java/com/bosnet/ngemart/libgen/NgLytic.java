package com.bosnet.ngemart.libgen;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.AddToCartEvent;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.PurchaseEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.crashlytics.android.answers.StartCheckoutEvent;

import java.math.BigDecimal;
import java.util.Currency;

interface runSafe {
    void run() throws Exception;
}

public class NgLytic {

    private static void async(runSafe runSafe) {
        new Thread(() -> {
            try {
                runSafe.run();
            } catch (Exception ignored) {
            }
        }).start();
    }

    public static void trackAddCart(String productId, String productName, String type, int price, int qty, Boolean isSuggestionOrder, String action) {
        async(() -> Answers.getInstance().logAddToCart(new AddToCartEvent()
                .putItemId(productId)
                .putItemName(productName)
                .putItemType(type)
                .putItemPrice(BigDecimal.valueOf(price))
                .putCurrency(Currency.getInstance("IDR"))
                .putCustomAttribute("qty", qty)
                .putCustomAttribute("isSuggestionOrder", isSuggestionOrder.toString())
                .putCustomAttribute("action", action)));
    }

    public static void trackException(Exception e) {
        async(() -> Crashlytics.logException(e));
    }

    public static void trackLogin(String signInMethod, boolean isSuccess) {
        async(() -> Answers.getInstance().logLogin(new LoginEvent()
                .putMethod(signInMethod)
                .putSuccess(isSuccess)));
    }

    public static void trackLogout(String email) {
        async(() -> Answers.getInstance().logCustom(new CustomEvent("Logout")
                .putCustomAttribute("email", email)));
    }

    public static void trackContent(Object object, String name, String module) {
        async(() -> Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(name)
                .putContentType(module)
                .putContentId(object.getClass().getSimpleName())
        ));
    }

    public static void trackSearch(String keyword) {
        async(() -> Answers.getInstance().logSearch(new SearchEvent()
                .putQuery(keyword)));
    }

    public static void setIdUserCrash(String id, String email, String fullName) {
        async(() -> {
            Crashlytics.setUserIdentifier(id);
            Crashlytics.setUserEmail(email);
            Crashlytics.setUserName(fullName);
        });
    }

    public static void trackStartCheckout(String id, int totalItem, double totalPrice, String type) {
        async(() -> {
            Answers.getInstance().logStartCheckout(new StartCheckoutEvent()
                    .putTotalPrice(BigDecimal.valueOf(totalPrice))
                    .putCurrency(Currency.getInstance("IDR"))
                    .putItemCount(totalItem)
                    .putCustomAttribute("id", id)
                    .putCustomAttribute("mode", type));
        });
    }

    public static void trackPurchase(String itemId, String itemName, String categoryId, double price, int qty,
                                     String paymentMethod, String checkoutId, String cartId, Boolean isSuggestion,
                                     Boolean isSuccess) {
        async(() -> {
            Answers.getInstance().logPurchase(new PurchaseEvent()
                    .putItemId(itemId)
                    .putItemName(itemName)
                    .putItemPrice(BigDecimal.valueOf(qty * price))
                    .putItemType(categoryId)
                    .putCurrency(Currency.getInstance("IDR"))
                    .putSuccess(isSuccess)
                    .putCustomAttribute("paymentMethod", paymentMethod)
                    .putCustomAttribute("checkoutId", checkoutId)
                    .putCustomAttribute("cartId", cartId)
                    .putCustomAttribute("isSuggestion", isSuggestion.toString()));
        });
    }

}
