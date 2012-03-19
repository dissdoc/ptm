class PhotosController < ApplicationController

  def new
    @album = Album.find(params[:album_id])
    @photo = @album.photos.new
  end
end