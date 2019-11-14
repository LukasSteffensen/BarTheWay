package com.p3.bartheway.Browse;

import com.p3.bartheway.Item;

import java.util.List;

public interface BrowseView {
    void showLoading();
    void hideLoading();
    void onGetResult(List<Item> items);
    void onErrorLoading(String message);
}
