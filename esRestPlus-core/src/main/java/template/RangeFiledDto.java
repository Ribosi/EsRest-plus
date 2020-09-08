package template;

import java.util.Date;

public class RangeFiledDto {
    private boolean isDate = false;
    private Object from;
    private Object to;
    private boolean includeFrom;
    private boolean includeTo;

    public RangeFiledDto(Object from, Object to) {
        this(from, to, true, true);
    }

    public RangeFiledDto(Object from, Object to, boolean includeFrom, boolean includeTo) {
        if (from instanceof Date && to instanceof Date) {
            isDate = true;
        }
        this.from = from;
        this.to = to;
        this.includeFrom = includeFrom;
        this.includeTo = includeTo;
    }

    public boolean isIncludeFrom() {
        return includeFrom;
    }

    public void setIncludeFrom(boolean includeFrom) {
        this.includeFrom = includeFrom;
    }

    public boolean isIncludeTo() {
        return includeTo;
    }

    public void setIncludeTo(boolean includeTo) {
        this.includeTo = includeTo;
    }

    public boolean isDate() {
        return isDate;
    }

    public void setDate(boolean date) {
        isDate = date;
    }

    public Object getFrom() {
        return from;
    }

    public void setFrom(Object from) {
        this.from = from;
    }

    public Object getTo() {
        return to;
    }

    public void setTo(Object to) {
        this.to = to;
    }
}
