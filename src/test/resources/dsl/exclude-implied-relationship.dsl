workspace {

    model {
        softwareSystem "A"

        softwareSystem "B" {
            b = container "B"
        }

        r = a -> b
    }

    views {
        systemLandscape {
            include *
            exclude r
        }
    }

}