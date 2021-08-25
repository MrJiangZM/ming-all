package com.ming.blog.cache;

public class BaseCo {

    private boolean nullValue = false;

    public boolean isNullValue() {
        return nullValue;
    }

    public void setNullValue(boolean nullValue) {
        this.nullValue = nullValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BaseCo that = (BaseCo) o;
        if (nullValue && that.nullValue) {
            return true;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        if (nullValue) {
            return 1;
        }
        return super.hashCode();
    }

}
