class NotesController < ApplicationController
  before_filter :authenticate_user!

  def create
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:photo_id])
    @note = @photo.notes.new(params[:note])
    @note.user_id = current_user.id
    if @note.save!
      redirect_to [@album, @photo]
    else
      redirect_to root_path
    end
  end

  def destroy
    @album = Album.find(params[:album_id])
    @photo = @album.photos.find(params[:photo_id])
    @note = current_user.notes.find(params[:id])
    @note.destroy
    redirect_to [@album, @photo]
  end
end