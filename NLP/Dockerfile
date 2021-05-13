# Copyright (c) Jupyter Development Team.
# Distributed under the terms of the Modified BSD License.
ARG BASE_CONTAINER=jupyter/scipy-notebook
FROM $BASE_CONTAINER

LABEL maintainer="Jupyter Project <jupyter@googlegroups.com>"

# Fix DL4006
SHELL ["/bin/bash", "-o", "pipefail", "-c"]

USER root

# Install the Spacy English model
COPY requirements.txt requirements.txt
RUN pip3 install -r requirements.txt
RUN python -m spacy download en_core_web_sm

WORKDIR $HOME