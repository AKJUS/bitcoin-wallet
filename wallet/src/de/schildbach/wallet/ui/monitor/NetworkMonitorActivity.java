/*
 * Copyright the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.ui.monitor;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import de.schildbach.wallet.R;
import de.schildbach.wallet.ui.AbstractWalletActivity;
import de.schildbach.wallet.util.ViewPagerTabs;
import de.schildbach.wallet.util.ZoomOutPageTransformer;

/**
 * @author Andreas Schildbach
 */
public final class NetworkMonitorActivity extends AbstractWalletActivity {
    private static final int POSITION_PEER_LIST = 0;
    private static final int POSITION_BLOCK_LIST = 1;
    private static final int[] TAB_LABELS = { R.string.network_monitor_peer_list_title,
            R.string.network_monitor_block_list_title };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        EdgeToEdge.enable(this, SystemBarStyle.dark(getColor(R.color.bg_action_bar)),
                SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.network_monitor_content);
        final Toolbar appbar = findViewById(R.id.network_monitor_appbar);
        appbar.getNavigationIcon().setTint(getColor(R.color.fg_on_dark_bg_network_significant));
        setActionBar(appbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        final ViewPager2 pager = findViewById(R.id.network_monitor_pager);
        final ViewPagerTabs pagerTabs = findViewById(R.id.network_monitor_pager_tabs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.network_monitor_group), (v, windowInsets) -> {
            final Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), insets.top, v.getPaddingRight(), v.getPaddingBottom());
            return windowInsets;
        });

        pagerTabs.addTabLabels(TAB_LABELS);

        final boolean twoPanes = getResources().getBoolean(R.bool.network_monitor_two_panes);

        if (twoPanes) {
            final RecyclerView recyclerView = (RecyclerView) pager.getChildAt(0);
            recyclerView.setClipToPadding(false);
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                final int width = recyclerView.getWidth();
                recyclerView.setPadding(0, recyclerView.getPaddingTop(), width / 2, recyclerView.getPaddingBottom());
                pager.setCurrentItem(0);
            });
            pager.setUserInputEnabled(false);
            pagerTabs.setMode(ViewPagerTabs.Mode.STATIC);
        } else {
            pager.setPageTransformer(new ZoomOutPageTransformer());
            pager.registerOnPageChangeCallback(pagerTabs.getPageChangeCallback());
            pagerTabs.setMode(ViewPagerTabs.Mode.DYNAMIC);
        }

        pager.setOffscreenPageLimit(1);
        pager.setAdapter(new PagerAdapter());
    }

    private class PagerAdapter extends FragmentStateAdapter {
        public PagerAdapter() {
            super(NetworkMonitorActivity.this);
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        @NonNull
        @Override
        public Fragment createFragment(final int position) {
            if (position == POSITION_PEER_LIST)
                return new PeerListFragment();
            else if (position == POSITION_BLOCK_LIST)
                return new BlockListFragment();
            else
                throw new IllegalArgumentException();
        }
    }
}
