
import pygame

pygame.font.init()

points_font = pygame.font.SysFont("ubuntumono", 108, bold=True)
button_font = pygame.font.SysFont("ubuntumono", 28)
color = (220, 220, 220)

def points_text(text, filename):
    pygame.image.save(points_font.render(text, True, (220, 220, 220)), filename)

def button_text(text, filename):
    text_surface = button_font.render(text, True, (40, 40, 40, 40))
    full_surface = pygame.surface.Surface(text_surface.get_rect()[2:], pygame.SRCALPHA, 32)
    full_surface.fill((173, 216, 230, 190))
    full_surface.blit(text_surface, (0,0))
    pygame.image.save(full_surface, filename)

for i in xrange(10):
    points_text(str(i), "%i.png"%i)

points_text("points: ", "points.png")
button_text("  exit to menu  ", "exit.png")
button_text("   pause  (P)   ", "pause.png")
