
instruction_text = u"""\
        Controls:
        
turning       left / right arrows
speeding up   up arrow
slowing down  down arrow
pause         P key

        Goal:

You were piloting your spaceship through the asteroid belt when you were ambushed by drones. Unfortunately, you have no missiles or other means of defending yourself. Fortunately, the drones are very stupid because their alien programmers ran out of time, so they na\xefvely travel in a straight line towards you to attack. Use the environment to your advantage to defeat the drones. Pilot your spaceship to lure the drones towards asteroids to destroy them. You get many points for each drone destroyed this way. Avoid crashing into drones and asteroids, as this reduces your health and earns you no points. Try to survive for as long as possible.
"""

import AcmeWordSmasher
import pygame

pygame.font.init()

points_font = pygame.font.SysFont("ubuntumono", 64, bold=True)
button_font = pygame.font.SysFont("ubuntumono", 28)
color = (220, 220, 220)

def points_text(text, filename):
    pygame.image.save(points_font.render(text, True, (220, 220, 220)), filename)

def button_text(text, filename):
    text_surface = button_font.render(text, True, (40, 40, 40))
    full_surface = pygame.surface.Surface(text_surface.get_rect()[2:], pygame.SRCALPHA, 32)
    full_surface.fill((173, 216, 230, 190))
    full_surface.blit(text_surface, (0,0))
    pygame.image.save(full_surface, filename)

for i in xrange(10):
    points_text(str(i), "%i.png"%i)

points_text("points: ", "points.png")
button_text("  exit to menu  ", "exit.png")
button_text("   pause  (P)   ", "pause.png")
button_text(" ~~  CHEAT  ~~ ", "cheat.png")

button_text("  easy      (1)  ", "easy.png")
button_text("  medium    (2)  ", "medium.png")
button_text("  hard      (3)  ", "hard.png")
button_text("  very hard (4)  ", "veryHard.png")
button_text("  previous song  ", "previousSong.png")
button_text("  next song      ", "nextSong.png")

instruction_lines = AcmeWordSmasher.justify(instruction_text, 60).split('\n')

dy = 30
instruction_surface = pygame.surface.Surface((900, dy * (1+len(instruction_lines))), pygame.SRCALPHA, 32)
instruction_surface.fill((173, 216, 230, 190))

for i, line in enumerate(instruction_lines):
    instruction_surface.blit(button_font.render(line, True, (40, 40, 40)), (30, dy*(i+1)))

pygame.image.save(instruction_surface, "instructions.png")

