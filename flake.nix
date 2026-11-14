{
  description = "Browser port of Widener's Baby Punting Adventure";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";

  outputs = { nixpkgs, ... }:
    let
      systems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];
      forAllSystems = nixpkgs.lib.genAttrs systems;
    in
    {
      devShells = forAllSystems (system:
        let
          pkgs = import nixpkgs { inherit system; };
        in
        {
          default = pkgs.mkShell {
            packages = [
              pkgs.nodejs_24
              pkgs.corepack
              pkgs.just
              pkgs.git
              pkgs.ffmpeg
              pkgs.jdk21
              pkgs.nixfmt
            ];
            shellHook = ''
              export COREPACK_ENABLE_DOWNLOAD_PROMPT=0
            '';
          };
        });

      formatter = forAllSystems (system: (import nixpkgs { inherit system; }).nixfmt);
    };
}
