import numpy as np
import cv2
import tensorflow as tf

def get_frozen_graph(graph_file):
    with tf.gfile.FastGFile(graph_file, "rb") as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
    return graph_def


def draw_label(image, point, label, font=cv2.FONT_HERSHEY_SIMPLEX, font_scale=0.5, thickness=2):
    size = cv2.getTextSize(label, font, font_scale, thickness)[0]
    x, y = point
    cv2.rectangle(image, (x, y - size[1]),
                  (x + size[0], y), (255, 0, 0), cv2.FILLED)
    cv2.putText(image, 'Bump'+label, point, font, font_scale,
                (255, 255, 255), thickness)


def non_max_suppression(boxes, probs=None, nms_threshold=0.3):
    """Non-max suppression

    Arguments:
        boxes {np.array} -- a Numpy list of boxes, each one are [x1, y1, x2, y2]
    Keyword arguments
        probs {np.array} -- Probabilities associated with each box. (default: {None})
        nms_threshold {float} -- Overlapping threshold 0~1. (default: {0.3})

    Returns:
        list -- A list of selected box indexes.
    """
    # if there are no boxes, return an empty list
    if len(boxes) == 0:
        return []

    # if the bounding boxes are integers, convert them to floats -- this
    # is important since we'll be doing a bunch of divisions
    if boxes.dtype.kind == "i":
        boxes = boxes.astype("float")

    # initialize the list of picked indexes
    pick = []

    # grab the coordinates of the bounding boxes
    x1 = boxes[:, 0]
    y1 = boxes[:, 1]
    x2 = boxes[:, 2]
    y2 = boxes[:, 3]

    # compute the area of the bounding boxes and grab the indexes to sort
    # (in the case that no probabilities are provided, simply sort on the
    # bottom-left y-coordinate)
    area = (x2 - x1 + 1) * (y2 - y1 + 1)
    idxs = y2

    # if probabilities are provided, sort on them instead
    if probs is not None:
        idxs = probs

    # sort the indexes
    idxs = np.argsort(idxs)

    # keep looping while some indexes still remain in the indexes list
    while len(idxs) > 0:
        # grab the last index in the indexes list and add the index value
        # to the list of picked indexes
        last = len(idxs) - 1
        i = idxs[last]
        pick.append(i)

        # find the largest (x, y) coordinates for the start of the bounding
        # box and the smallest (x, y) coordinates for the end of the bounding
        # box
        xx1 = np.maximum(x1[i], x1[idxs[:last]])
        yy1 = np.maximum(y1[i], y1[idxs[:last]])
        xx2 = np.minimum(x2[i], x2[idxs[:last]])
        yy2 = np.minimum(y2[i], y2[idxs[:last]])

        # compute the width and height of the bounding box
        w = np.maximum(0, xx2 - xx1 + 1)
        h = np.maximum(0, yy2 - yy1 + 1)

        # compute the ratio of overlap
        overlap = (w * h) / area[idxs[:last]]

        # delete all indexes from the index list that have overlap greater
        # than the provided overlap threshold
        idxs = np.delete(idxs, np.concatenate(([last],
                                               np.where(overlap > nms_threshold)[0])))
    # return only the bounding boxes indexes
    return pick


def visualize_detection(image, num_detections, classes, boxes, scores):
	boxes_pixels = []
	for i in range(num_detections):
	    # scale box to image coordinates
	    box = boxes[i] * np.array([image.shape[0], image.shape[1], 
	    						   image.shape[0], image.shape[1]])
	    box = np.round(box).astype(int)
	    boxes_pixels.append(box)
	boxes_pixels = np.array(boxes_pixels)

	# Remove overlapping boxes with non-max suppression, return picked indexes.
	pick = non_max_suppression(boxes_pixels, scores[:num_detections], 0.5)
	# print(pick)


	for i in pick:
	    box = boxes_pixels[i]
	    box = np.round(box).astype(int)
	    # Draw bounding box.
	    image = cv2.rectangle(
	        image, (box[1], box[0]), (box[3], box[2]), (0, 255, 0), 2)
	    label = "{}:{:.2f}".format(int(classes[i]), scores[i])
	    # Draw label (class index and probability).
	    draw_label(image, (box[1], box[0]), label)
