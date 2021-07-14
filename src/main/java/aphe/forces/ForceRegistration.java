package aphe.forces;

import aphe.rigidbody.Rigidbody2D;

public class ForceRegistration {
    public ForceGenerator fg;
    public Rigidbody2D rb;

    public ForceRegistration(ForceGenerator fg, Rigidbody2D rb) {
        this.fg = fg;
        this.rb = rb;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj.getClass() != ForceRegistration.class)
            return false;

        ForceRegistration fr = (ForceRegistration) obj;
        return fr.rb == this.rb && fr.fg == this.fg;
    }
}
