package com.p3.bartheway.Browse;

import com.p3.bartheway.Database.Item;
import com.p3.bartheway.Database.Loan;
import com.p3.bartheway.Database.Student;

import java.util.List;

public interface BrowseView {
    void showLoading();
    void hideLoading();
    void onGetResult(List<Item> items);
    void onErrorLoading(String message);
    void onGetLoans(List<Loan> loans);
    void onGetStudent(List<Student> student);
}