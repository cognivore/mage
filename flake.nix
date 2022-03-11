{
    inputs = {
        nixpkgs.url = "github:nixos/nixpkgs";
    };

    outputs = {self, nixpkgs}:
        let pkgs = nixpkgs.legacyPackages.x86_64-linux;
            ppkgs = pkgs.perlPackages;
        in {
            defaultPackage.x86_64-linux = pkgs.hello;

            devShell.x86_64-linux =
                pkgs.mkShell { buildInputs = [
                    pkgs.openjdk11
                    pkgs.jre
                    pkgs.maven
                    pkgs.yq
                    pkgs.hello
                    pkgs.perl
                    ppkgs.ArchiveExtract
                    ppkgs.ArchiveZip
                ]; };
        };
}
