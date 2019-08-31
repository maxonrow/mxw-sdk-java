package com.mxw.protocol.response;


import com.mxw.utils.Numeric;

public class TransactionFeeSetting {

    public TransactionFeeSetting(){

    }

    private Numeric min;

    private Numeric max;

    private Numeric percentage;

    public Numeric getMin() {
        return min;
    }

    public void setMin(Numeric min) {
        this.min = min;
    }

    public Numeric getMax() {
        return max;
    }

    public void setMax(Numeric max) {
        this.max = max;
    }

    public Numeric getPercentage() {
        return percentage;
    }

    public void setPercentage(Numeric percentage) {
        this.percentage = percentage;
    }
}
