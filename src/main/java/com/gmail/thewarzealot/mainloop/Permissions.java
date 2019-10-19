package com.gmail.thewarzealot.mainloop;

class Permissions {

    public enum Perm {
        TEST("mainloop.test"),
        RELOAD("mainloop.reload");

        private String perm;

        Perm(String perm) {
            this.perm = perm;
        }

        public String getPerm() {
            return perm;
        }
    }
}
