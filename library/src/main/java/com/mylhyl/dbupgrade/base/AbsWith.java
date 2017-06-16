package com.mylhyl.dbupgrade.base;

/**
 * Created by hupei on 2017/6/16.
 */

public abstract class AbsWith<T extends BaseTable> {
    protected int mOldVersion;
    private int mNewVersion;
    protected int mUpgradeVersion;

    public AbsWith(int mOldVersion, int mNewVersion) {
        this.mOldVersion = mOldVersion;
        this.mNewVersion = mNewVersion;
    }

    final void addOldVersion() {
        mOldVersion++;
    }

    final boolean isUpgrade() {
        return mOldVersion == mUpgradeVersion && mUpgradeVersion < mNewVersion;
    }

    protected abstract void setUpgradeVersion(int upgradeVersion);

    protected abstract void addUpgrade(T upgradeTable);

    protected abstract void clearUpgradeList();

    protected abstract void upgrade();

}
