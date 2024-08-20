/*
 * This is a combined version of the following workspaces:
 *
 * - "Big Bank plc - System Landscape" (https://structurizr.com/share/28201/)
 * - "Big Bank plc - Internet Banking System" (https://structurizr.com/share/36141/)
*/
workspace {

    model {
        user = person "User"
        softwareSystem1 = softwareSystem "Software System"

        user -> softwareSystem1 "Uses"
    }

    views {
        systemContext softwareSystem1 {
            include *
            autolayout
        }
    }
    
}