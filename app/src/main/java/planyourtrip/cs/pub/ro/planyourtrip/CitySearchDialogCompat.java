package planyourtrip.cs.pub.ro.planyourtrip;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;

public class CitySearchDialogCompat<T extends Searchable> extends BaseSearchDialogCompat<T> {
    private String title;
    private String searchHint;
    private SearchResultListener<T> searchResultListener;

    public CitySearchDialogCompat(Context context, String title, String searchHint,
                                  @Nullable Filter filter, ArrayList<T> items,
                                  SearchResultListener<T> searchResultListener) {
        super(context, items, filter, null, null);
        this.title = title;
        this.searchHint = searchHint;
        this.searchResultListener = searchResultListener;
    }

    @Override
    protected void getView(View view) {
        setContentView(view);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(true);
        TextView txtTitle = view.findViewById(ir.mirrajabi.searchdialog.R.id.txt_title);
        final EditText searchBox = view.findViewById(getSearchBoxId());
        txtTitle.setText(this.title);
        searchBox.setHint(this.searchHint);

        view.findViewById(ir.mirrajabi.searchdialog.R.id.dummy_background)
                .setOnClickListener(view1 -> dismiss());

        final CitySearchModelAdapter adapter = new CitySearchModelAdapter(getContext(),
                R.layout.search_list_item, getItems());
        adapter.setSearchResultListener(this.searchResultListener);
        adapter.setSearchDialog(this);
        setFilterResultListener(items -> ((CitySearchModelAdapter<T>) getAdapter())
                .setSearchTag(searchBox.getText().toString())
                .setItems(items));
        setAdapter(adapter);
    }

    public CitySearchDialogCompat<T> setTitle(String title) {
        this.title = title;
        return this;
    }

    @LayoutRes
    @Override
    protected int getLayoutResId() {
        return ir.mirrajabi.searchdialog.R.layout.search_dialog_compat;
    }

    @IdRes
    @Override
    protected int getSearchBoxId() {
        return ir.mirrajabi.searchdialog.R.id.txt_search;
    }

    @IdRes
    @Override
    protected int getRecyclerViewId() {
        return ir.mirrajabi.searchdialog.R.id.rv_items;
    }
}
