





## References

[How to handle bullets in Box2D](http://www.iforce2d.net/b2dtut/collision-callbacks)
[How to collision filter with groups](http://www.aurelienribon.com/blog/2011/07/box2d-tutorial-collision-filtering/)
[How to make lightning effect](http://photoshopcafe.com/tutorials/lightning/lightning.htm)


## TODO LIST

- Title/Intro Screen
- Audio effects / Soundtrack
- Pointers to bases
- Monetization strategy
- A.I. for main enemy
- Add support units, dropjets and tanks
- Add alien jumping blob invasion level. Meteors strikes release blobs.
- Two-player / Leaderboard
- Optimize/Auto-adjust (especially for WebGL version)
- Engine animation (PNG anime or particle effect?)
- Add FPSLogger and in-game physics changing / planetoid creation tools
- Combine walk and wing images for walking and transforming


## STEPS TAKEN

- Getting basics physics done in Box2D debug renderer mode. Planetoids and circles
for the actors.
- Made temporary procedural graphics for objects
- Added star field background to move in slowed parallax relative to player
- Created a planetoid image in PhotoShop and added to game
- Created a walking robot animation in PhotoShop and added to game
- Created Intro screen (star splash).
- Changed to less realistic physics; "fictitious physics". Surface of any size body has same gravity, but smaller bodies
decrease faster the further a small body moves. f(dist) = 4000 * (0.5)^(dist/radius)




* I was tweaking physics and game-play throughout.