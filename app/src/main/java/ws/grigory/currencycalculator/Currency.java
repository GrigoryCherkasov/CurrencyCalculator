package ws.grigory.currencycalculator;

import static ws.grigory.currencycalculator.WidgetParameters.checkInfinity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Currency implements Parcelable, Cloneable {
    public String name;
    public float rate = 1;
    public float count = 1;
    public float value = 0;

    public Currency(String name, float rate, float count) {
        this.name = name;
        this.rate = rate;
        this.count = count;
    }

    public Currency(String name) {
        this.name = name;
    }

    public float getBaseValue() {
        return checkInfinity((value / count) * rate);
    }

    public void calculateValue(float baseValue) {
        this.value = checkInfinity((baseValue / rate) * count);
    }

    public void plus(float value) {
        this.value = checkInfinity(value + this.value);
    }

    public void minus(float value) {
        this.value = checkInfinity(value - this.value);
    }

    public void mul(float value) {
        this.value = checkInfinity(value * this.value);
    }

    public void div(float value) {
        if (this.value == 0) {
            this.value = value < 0 ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        } else {
            this.value = checkInfinity(value / this.value);
        }
    }

    public boolean isInfinity() {
        return value == Float.NEGATIVE_INFINITY || value == Float.POSITIVE_INFINITY;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected Currency(Parcel in) {
        name = in.readString();
        rate = in.readFloat();
        count = in.readFloat();
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeFloat(rate);
        parcel.writeFloat(count);
    }

    @NonNull
    @Override
    public Currency clone() {
        try {
            return (Currency) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
