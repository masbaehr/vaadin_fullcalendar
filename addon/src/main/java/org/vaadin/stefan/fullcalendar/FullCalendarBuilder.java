/*
 * Copyright 2018, Stefan Uebe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.vaadin.stefan.fullcalendar;

/**
 * This class can be used to create FullCalendar instances via a fluent builder api. Every method returns
 * an immutable builder instance.
 */
public class FullCalendarBuilder {

    private boolean scheduler;
    private int entryLimit;

    /**
     * Creates a new builder instance with default settings.
     * @return builder instance
     */
    public static FullCalendarBuilder create() {
        return new FullCalendarBuilder(false, -1);
    }

    private FullCalendarBuilder(boolean scheduler, int entryLimit) {
        this.scheduler = scheduler;
        this.entryLimit = entryLimit;
    }

    /**
     * Activates Scheduler support.
     * <p/>
     * <b>Note: </b> You need to add the FullCalender Scheduler extension addon to the class path, otherwise
     * the build will fail.
     * @return new immutable instance with updated settings
     */
    public FullCalendarBuilder withScheduler() {
        return new FullCalendarBuilder(true, entryLimit);
    }

    /**
     * Expects the default limit of entries shown per day. This does not affect basic or
     * list views.
     * <p/>
     * Passing a negative number or 0 disabled the entry limit (same as not using this method at all).
     * @param entryLimit limit
     * @return new immutable instance with updated settings
     */
    public FullCalendarBuilder withEntryLimit(int entryLimit) {
        return new FullCalendarBuilder(scheduler, entryLimit);
    }

    /**
     * Builds the FullCalendar with the settings of this instance. Depending on some settings the returned
     * instance might be a subclass of {@link FullCalendar}.
     * @return FullCalendar instance
     */
    public FullCalendar build() {
        if (scheduler) {
            return createFullCalendarSchedulerInstance(entryLimit);
        } else {
            return createFullCalendarBasicInstance();
        }
    }

    protected FullCalendar createFullCalendarBasicInstance() {
        return new FullCalendar(entryLimit);
    }

    protected FullCalendar createFullCalendarSchedulerInstance(int entryLimit) {
        try {
            Class<?> loadClass = getClass().getClassLoader().loadClass("org.vaadin.stefan.fullcalendar.FullCalendarScheduler");
            return (FullCalendar) loadClass.getDeclaredConstructor(int.class).newInstance(this.entryLimit);
        } catch (ClassNotFoundException ce) {
            throw new ExtensionNotFoundException("Could not find scheduler extension for FullCalendar on class path. Please check you libraries / dependencies.", ce);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Thrown when an extension should be used that is not installed properly.
     */
    public static class ExtensionNotFoundException extends RuntimeException {
        public ExtensionNotFoundException() {
        }

        public ExtensionNotFoundException(String message) {
            super(message);
        }

        public ExtensionNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public ExtensionNotFoundException(Throwable cause) {
            super(cause);
        }

        public ExtensionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

}
