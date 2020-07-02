package com.portrait;

/**
 * @author:yjc
 * @Date: 2019/9/3 10:41
 * @Description:
 */
public class Portrait {
    //男权重
    private double male;
    //女权重
    private double female;

    public void portraitSex(double male2, double female2, long times){

        double sum = this.male + this.female + (male2 + female2) * times;
        if(sum != 0){
            this.male = (this.male + male2 * times) / sum;
            this.female = (this.female + female2 * times) / sum;
        }
    }

    public double getMale() {
        return male;
    }

    public void setMale(double male) {
        this.male = male;
    }

    public double getFemale() {
        return female;
    }

    public void setFemale(double female) {
        this.female = female;
    }
}
