package br.com.sobreiraromulo.poi.controller;

import org.springframework.web.bind.annotation.RestController;

import br.com.sobreiraromulo.poi.controller.dto.CreatePointOfInterest;
import br.com.sobreiraromulo.poi.controller.dto.PagedResponse;
import br.com.sobreiraromulo.poi.entity.PointOfInterest;
import br.com.sobreiraromulo.poi.repository.PointOfInterestRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@RestController
public class PointOfInterestController {

 private final PointOfInterestRepository pointOfInterestRepository;


 public PointOfInterestController(PointOfInterestRepository pointOfInterestRepository) {
  this.pointOfInterestRepository = pointOfInterestRepository;
 }


 @PostMapping("/points-of-interests")
 public ResponseEntity<?> createPoi(@RequestBody CreatePointOfInterest createPointOfInterest) {
     
    pointOfInterestRepository.save(new PointOfInterest(createPointOfInterest.name(), createPointOfInterest.x(), createPointOfInterest.y()));

     return ResponseEntity.ok().build();
 }

 @GetMapping("/points-of-interests")
 public ResponseEntity<PagedResponse<PointOfInterest>> listPoi(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize ) {
     
   var pois=  pointOfInterestRepository.findAll(PageRequest.of(page, pageSize));

   PagedResponse<PointOfInterest> response = new PagedResponse<>(pois);
   return ResponseEntity.ok(response);
 }

 @GetMapping("/near-me")
 public ResponseEntity<List<PointOfInterest>> nearMe(@RequestParam("x") Long x,
                                                     @RequestParam("y") Long y,
                                                     @RequestParam("dmax") Long dmax) {
     var xMin = x - dmax;
     var xMax = x + dmax;
     var yMin = y - dmax;
     var yMax = y + dmax;

     var body = pointOfInterestRepository.findNearMe(xMin, xMax, yMin, yMax)
             .stream()
             .filter(p -> distanceBetweenPoints(x, y, p.getX(), p.getY()) <= dmax)
             .toList();

     return ResponseEntity.ok(body);
 }

 private Double distanceBetweenPoints(Long x1, Long y1, Long x2, Long y2) {
     return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
 }
 
}
