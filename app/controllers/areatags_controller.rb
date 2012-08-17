class AreatagsController < ApplicationController
  before_filter :get_photo
  before_filter :get_areatag

  def show
    #respond_to do |format|
    #  format.json { render :text => @areatag.to_json }
    #end
    render :text => @areatag.to_json
  end

  protected

  def get_photo
    @photo = Photo.find(params[:photo_id])
  end

  def get_areatag
    @areatag = @photo.areatags.find(params[:areatag_id])
  end
end